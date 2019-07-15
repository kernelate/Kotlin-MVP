package com.cherry.home.ui.device.config

import com.cherry.home.ui.base.MvpView

interface DeviceBindModelView : MvpView {

    fun start()

    fun cancel()

    fun setEC(ssid: String, password: String, token: String)

    fun setAP(ssid: String, password: String, token: String)

    fun configFailure()
}