package com.kaanf.core.data.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import java.io.File
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** The encryption key never leaves Android Keystore; ciphertext is excluded from backup. */
class AndroidSecureSessionStore(context: Context) : SecureSessionStore {
    private val file = AtomicFile(File(context.noBackupFilesDir, "crew_session_v1.bin"))

    override suspend fun read(): String? = withContext(Dispatchers.IO) {
        if (!file.baseFile.exists() && !File(file.baseFile.path + ".bak").exists()) return@withContext null
        val bytes = file.readFully()
        if (bytes.size < 2 || bytes[0] != FORMAT_VERSION || bytes[1].toInt() != IV_BYTES ||
            bytes.size < 2 + IV_BYTES + TAG_BITS / 8
        ) {
            file.delete()
            return@withContext null
        }
        try {
            val key = keyStore().getKey(KEY_ALIAS, null) as? SecretKey
            if (key == null) {
                // A restored/orphaned file must never cause plaintext fallback or a crash loop.
                file.delete()
                return@withContext null
            }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, bytes.copyOfRange(2, 2 + IV_BYTES)))
            cipher.doFinal(bytes, 2 + IV_BYTES, bytes.size - 2 - IV_BYTES).decodeToString()
        } catch (_: AEADBadTagException) {
            file.delete()
            null
        } catch (_: KeyPermanentlyInvalidatedException) {
            discardInvalidatedKey()
            null
        } catch (_: UnrecoverableKeyException) {
            discardInvalidatedKey()
            null
        }
    }

    override suspend fun write(value: String): Unit = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        check(iv.size == IV_BYTES) { "Unexpected secure session IV size" }
        val encrypted = cipher.doFinal(value.encodeToByteArray())
        val output = file.startWrite()
        try {
            output.write(byteArrayOf(FORMAT_VERSION, IV_BYTES.toByte()))
            output.write(iv)
            output.write(encrypted)
            file.finishWrite(output)
        } catch (error: Throwable) {
            file.failWrite(output)
            throw error
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = keyStore()
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )
        }.generateKey()
    }

    private fun discardInvalidatedKey() {
        file.delete()
        keyStore().deleteEntry(KEY_ALIAS)
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private companion object {
        const val KEY_ALIAS = "com.kaanf.crew.session.v1"
        const val FORMAT_VERSION: Byte = 1
        const val IV_BYTES = 12
        const val TAG_BITS = 128
    }
}

actual fun excludeLegacyPreferencesFromBackup() = Unit // Explicit manifest rules exclude the old file.
