package com.cherry.home.ui.device.config.ec

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.sdk.bean.DeviceBean

interface ECBindView  : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showToast(deviceBean: DeviceBean)

    fun showSuccessPage()

    fun showFailurePage()

    fun showConnectPage()

    fun setConnectProgress(progress: Float, animationDuration: Int)

    fun showNetWorkFailurePage()

    fun showBindDeviceSuccessTip()

    fun showDeviceFindTip(gwId: String)

    fun showConfigSuccessTip()

    fun showBindDeviceSuccessFinalTip()

    fun setAddDeviceName(name: String)

    fun showMainPage()
    fun hideMainPage()
    fun showSubPage()
    fun hideSubPage()

    fun getHomeID() : Long

    //DeviceBindModel
    fun setEC(ssid : String, password: String, token : String)

    fun setAP(ssid : String, password: String, token : String)

    fun configFailure()

    fun cancel()

    fun start()

    fun destroy()

    fun gotoAddDeviceTipActivity()

    fun gotogoToEZActivity()

    fun getSSID() : String

    fun removeNetwork(currentSSID : String)
}