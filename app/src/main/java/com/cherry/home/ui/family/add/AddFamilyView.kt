package com.cherry.home.ui.family.add

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback

interface AddFamilyView : MvpView {

    fun saveFamily()

    fun showFailed()

    fun showLoading()

    fun hideLoading()

    fun createHome(homeName: String, longitude : Double, latidude : Double, geoName : String?, roomList: List<String>, callback: ITuyaHomeResultCallback)

    fun setLocation(location : String)

}