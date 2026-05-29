package com.kaanf.core.data.device

import com.kaanf.core.domain.provider.DeviceIdProvider
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

private const val DEVICE_ID_KEY = "installation_id"

class IosDeviceIdProvider : DeviceIdProvider {
    override fun getDeviceId(): String {
        val defaults = NSUserDefaults.standardUserDefaults
        val existingId = defaults.stringForKey(DEVICE_ID_KEY)

        if (existingId != null) {
            return existingId
        }

        val generatedId = NSUUID().UUIDString()
        defaults.setObject(generatedId, forKey = DEVICE_ID_KEY)
        return generatedId
    }
}
