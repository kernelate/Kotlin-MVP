package com.cherry.home.ui.scenario.scene.action

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.sdk.bean.DeviceBean

interface SActionView : MvpView {

//    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun updateDeviceData(myDevices: List<DeviceBean>)

}