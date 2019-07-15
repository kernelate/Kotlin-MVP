package com.cherry.home.ui.device.config.ec

import android.Manifest
import android.text.TextUtils
import android.util.Log
import com.cherry.home.R
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import org.w3c.dom.Text
import javax.inject.Inject

@ConfigPersistent
class ECPresenter @Inject constructor() : BasePresenter<ECView>() {

    companion object {
        internal val TAG = "ECPresenter"

        internal val PRIVATE_CODE = 1315
        internal val CODE_FOR_LOCATION_PERMISSION = 222

        internal val CONFIG_MODE = "config_mode"
        internal val CONFIG_PASSWORD = "config_password"
        internal val CONFIG_SSID = "config_ssid"
    }

    internal val EC_MODE = 1

    fun showLocation(){
        checkViewAttached()
        mvpView?.apply {
            val ssid = getWifiSsId()
            if (!TextUtils.isEmpty(ssid)) {
                return
            } else {
                if (isWifiDisabled()) {
                    return
                }
                if (!checkSystemGPSLocation()) {
                    return
                } else {
                    checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, CODE_FOR_LOCATION_PERMISSION)
                }
            }
        }

    }

    fun goNextStep(){
        mvpView?.apply {
            val passWord = getWifiPass()
            val ssid = getCurrentSSID()
            val isConnected = isNetworkAvailable()

            if(!isConnected || TextUtils.isEmpty(ssid)){
                showSnackbar(R.string.connect_phone_to_network)
            } else {
                val is5g = is5GHz(ssid!!)
                if(!is5g){
                    gotoBindDeviceActivity(ssid, passWord)
                } else {
                    showDialog(ssid, passWord)
                }
            }
        }
    }

    fun checkWifiNetworkStatus(){
        mvpView?.apply {
            val isConnected = isNetworkAvailable()
            if(isConnected){
                val currentSSID = getCurrentSSID()
                if(!TextUtils.isEmpty(currentSSID)){
                    setWifiSSID(currentSSID)
                    if(is5GHz(currentSSID)){
                        show5gTip()
                    } else {
                        hide5gTip()
                    }
                }
            } else {
                showNoWifi()
            }
        }

    }

}