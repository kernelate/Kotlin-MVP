package com.cherry.home.ui.device.location

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.RoomBean

interface DeviceLocationView : MvpView {

    fun showError(error_msg : String)

    fun getRoomList(roomList : MutableList<RoomBean>)
}