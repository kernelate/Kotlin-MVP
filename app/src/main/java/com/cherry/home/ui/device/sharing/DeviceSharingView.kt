package com.cherry.home.ui.device.sharing

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.SharedUserInfoBean

interface DeviceSharingView : MvpView {

    fun showError(error : String)

    fun getSharedUserList(sharedUserList: List<SharedUserInfoBean>)
}