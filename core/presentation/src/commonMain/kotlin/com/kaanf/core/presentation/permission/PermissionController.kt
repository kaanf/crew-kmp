package com.kaanf.core.presentation.permission

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION

class PermissionController(
    private val mokoPermissionsController: PermissionsController
) {
    suspend fun requestPermission(permission: Permission): PermissionState {
        return try {
            mokoPermissionsController.providePermission(permission.toMokoPermission())
            PermissionState.GRANTED
        } catch (_: DeniedAlwaysException) {
            PermissionState.PERMANENTLY_DENIED
        } catch(_: DeniedException) {
            PermissionState.DENIED
        } catch(_: RequestCanceledException) {
            PermissionState.DENIED
        }
    }

    /** Sistem dialogu tetiklemeden mevcut izin durumunu okur (ör. Ayarlar'dan dönüşte). */
    suspend fun checkPermission(permission: Permission): PermissionState {
        return when (mokoPermissionsController.getPermissionState(permission.toMokoPermission())) {
            dev.icerock.moko.permissions.PermissionState.Granted -> PermissionState.GRANTED
            dev.icerock.moko.permissions.PermissionState.DeniedAlways -> PermissionState.PERMANENTLY_DENIED
            dev.icerock.moko.permissions.PermissionState.Denied -> PermissionState.DENIED
            else -> PermissionState.NOT_DETERMINED
        }
    }

    fun openAppSettings() {
        mokoPermissionsController.openAppSettings()
    }
}

fun Permission.toMokoPermission(): dev.icerock.moko.permissions.Permission {
    return when(this) {
        Permission.CAMERA -> dev.icerock.moko.permissions.Permission.CAMERA
        Permission.REMOTE_NOTIFICATION -> dev.icerock.moko.permissions.Permission.REMOTE_NOTIFICATION
    }
}
