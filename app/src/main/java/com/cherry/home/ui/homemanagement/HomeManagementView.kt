package com.cherry.home.ui.homemanagement

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.HomeBean

interface HomeManagementView : MvpView {

    fun showError(error: String)

    fun getDataFromServer(homeBeanList: MutableList<HomeBean>)
}