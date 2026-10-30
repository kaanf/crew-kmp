@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.kaanf.core.data.session

import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.addressOf
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionarySetValue
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask
import platform.Security.SecItemAdd
import platform.Security.SecItemDelete
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/** ThisDeviceOnly prevents the session from migrating through iCloud/device backups. */
class IosSecureSessionStore internal constructor(
    private val serviceName: String = "com.kaanf.crew.session",
    private val accountName: String = "current-session-v1",
) : SecureSessionStore {
    override suspend fun read(): String? = withQuery { query ->
        CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)
        memScoped {
            val result = alloc<CFTypeRefVar>()
            result.value = null
            val status = SecItemCopyMatching(query, result.ptr)
            if (status == errSecItemNotFound) return@memScoped null
            check(status == errSecSuccess) { "Could not read secure session ($status)" }
            val data: CFDataRef = checkNotNull(result.value).reinterpret()
            try {
                val size = CFDataGetLength(data)
                check(size in 0..Int.MAX_VALUE.toLong()) { "Invalid secure session size" }
                CFDataGetBytePtr(data)!!.reinterpret<kotlinx.cinterop.ByteVar>().readBytes(size.toInt()).decodeToString()
            } finally {
                CFRelease(data)
            }
        }
    }

    override suspend fun write(value: String) {
        val bytes = value.encodeToByteArray()
        bytes.usePinned { pinned ->
            val data = checkNotNull(CFDataCreate(null, pinned.addressOf(0).reinterpret(), bytes.size.convert()))
            val attributes = checkNotNull(CFDictionaryCreateMutable(null, 0, null, null))
            try {
                CFDictionarySetValue(attributes, kSecValueData, data)
                CFDictionarySetValue(attributes, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
                withQuery { query ->
                    val status = SecItemUpdate(query, attributes)
                    if (status == errSecItemNotFound) {
                        CFDictionarySetValue(query, kSecValueData, data)
                        CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
                        val addStatus = SecItemAdd(query, null)
                        check(addStatus == errSecSuccess) { "Could not create secure session ($addStatus)" }
                    } else {
                        check(status == errSecSuccess) { "Could not update secure session ($status)" }
                    }
                }
            } finally {
                CFRelease(attributes)
                CFRelease(data)
            }
        }
    }

    /** Only isolated test namespaces may use deletion; production keeps a signed-out marker. */
    internal fun deleteTestEntry() {
        check(serviceName == "com.kaanf.crew.session.tests") { "Refusing to delete a production session" }
        withQuery { query ->
            val status = SecItemDelete(query)
            check(status == errSecSuccess || status == errSecItemNotFound) { "Could not clean test session ($status)" }
        }
    }

    private fun <T> withQuery(block: (CFMutableDictionaryRef) -> T): T {
        // Null callbacks deliberately borrow values; all owned CF values stay alive until the call ends.
        val query = checkNotNull(CFDictionaryCreateMutable(null, 0, null, null))
        val service = checkNotNull(CFStringCreateWithCString(null, serviceName, kCFStringEncodingUTF8))
        val account = checkNotNull(CFStringCreateWithCString(null, accountName, kCFStringEncodingUTF8))
        try {
            CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionarySetValue(query, kSecAttrService, service)
            CFDictionarySetValue(query, kSecAttrAccount, account)
            return block(query)
        } finally {
            CFRelease(query)
            CFRelease(service)
            CFRelease(account)
        }
    }
}

actual fun excludeLegacyPreferencesFromBackup() {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        NSDocumentDirectory, NSUserDomainMask, null, false, null,
    ) ?: return
    val path = checkNotNull(directory.path) + "/prefs.preferences_pb"
    if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return
    val file = NSURL.fileURLWithPath(path)
    check(file.setResourceValue(true, NSURLIsExcludedFromBackupKey, null)) {
        "Could not exclude legacy preferences from backup"
    }
}
