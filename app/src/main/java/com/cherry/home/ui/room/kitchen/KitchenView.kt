package com.cherry.home.ui.room.kitchen

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.sdk.bean.DeviceBean

interface KitchenView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun setData(deviceList : List<DeviceBean>)
}