package com.cherry.home.ui.device.category.sensor


import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.device.view.DeviceView
import javax.inject.Inject

@ConfigPersistent
class SensorPresenter @Inject constructor() : BasePresenter<DeviceView>() {

    val listSocket = arrayOf("Electric Outlet", "Electric Outlet (Bluetooth)", "Electric Outlet (Zigbee)", "Multiple Socket", "Smart Socket","Smart Power Strip")
}