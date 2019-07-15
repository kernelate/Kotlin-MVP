package com.cherry.home.ui.room.diningroom

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.sdk.bean.DeviceBean

interface DiningRoomView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun setData(deviceList : List<DeviceBean>)
}