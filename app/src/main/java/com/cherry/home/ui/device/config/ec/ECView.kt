package com.cherry.home.ui.device.config.ec

import com.cherry.home.ui.base.MvpView

interface ECView : MvpView {

    fun showSnackbar(msg : Int)

    fun setWifiSSID(ssId: String)

    fun setWifiPass(pass: String)

    fun getWifiPass(): String

    fun getWifiSsId(): String

    fun showNoWifi()

    fun show5gTip()

    fun hide5gTip()

    fun isWifiDisabled() : Boolean

    fun checkSystemGPSLocation() : Boolean

    fun checkSinglePermission(permission : String, resultCode: Int) : Boolean

    fun getCurrentSSID() : String

    fun isNetworkAvailable() : Boolean

    fun is5GHz(ssid: String): Boolean

    fun gotoBindDeviceActivity(ssid: String, passWord: String)

    fun showDialog(ssid : String, passWord : String)

}