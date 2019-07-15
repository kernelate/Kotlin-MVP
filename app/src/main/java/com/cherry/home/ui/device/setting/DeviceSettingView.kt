package com.cherry.home.ui.device.setting

import com.cherry.home.ui.base.MvpView

interface DeviceSettingView: MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(code: String, msg : String)

    fun dialogSettingRemoveDevice()

    fun dialogRenameDevice()

    fun renameDevice(titleName : String)

    fun gotoMainActivity()

    fun setDeviceName(devName : String)

    fun setDeviceLocation(room : String)
}