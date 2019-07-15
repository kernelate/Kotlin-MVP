package com.cherry.home.ui.device.config.util

import android.text.TextUtils
import com.tuya.smart.android.device.utils.WiFiUtil
import com.tuya.smart.sdk.TuyaSdk

object BindDeviceUtils {
    fun isAPMode(): Boolean {
        var ssid = ""
        if (TextUtils.isEmpty(ssid)) {
            ssid = CommonConfig.DEFAULT_COMMON_AP_SSID
        }
        val currentSSID = WiFiUtil.getCurrentSSID(TuyaSdk.getApplication()).toLowerCase()
        return !TextUtils.isEmpty(currentSSID) && (currentSSID.startsWith(ssid.toLowerCase()) ||
                currentSSID.startsWith(CommonConfig.DEFAULT_OLD_AP_SSID.toLowerCase()) ||
                currentSSID.contains(CommonConfig.DEFAULT_KEY_AP_SSID.toLowerCase()))
    }
}