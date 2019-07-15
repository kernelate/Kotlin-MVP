package com.cherry.home.ui.scenario.automation.action

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.sdk.bean.DeviceBean

interface AActionView : MvpView {


    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)

    fun updateDeviceData(myDevices: List<DeviceBean>)
}