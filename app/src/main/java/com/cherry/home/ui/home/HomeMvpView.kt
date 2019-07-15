package com.cherry.home.ui.home

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.android.panel.api.ITuyaPanelLoadCallback
import com.tuya.smart.sdk.bean.DeviceBean

interface HomeMvpView: MvpView {

    fun showProgress(show: Boolean)

    fun showProgress()

    fun hideProgress()

    fun showError(error: Throwable)

    fun showError(string: String)

    fun gotoFamilyActivity()

    fun loadStart()

    fun loadFinish()

    fun hideBackgroundView()

    fun showBackgroundView()

    fun gotoCreateHome()

    fun updateDeviceData(myDevices: List<DeviceBean>)

    fun gotoPanelViewController(homeID : Long, devId: String, callback : ITuyaPanelLoadCallback)

    fun gotoDeviceSetting(devId: String)


}