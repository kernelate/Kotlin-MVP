package com.cherry.home.ui.main

import android.support.v4.app.Fragment
import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback

interface MainMvpView: MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(code: String, msg : String)

    fun gotoFamilyActivity()

    fun getCurrentHome() : HomeBean?

    fun setFamilyName(name : String)

    fun queryHomeList(callback : ITuyaGetHomeListCallback)

    fun setUsername(username : String)

    fun showHamburgerIcon()

    fun showBackIcon(isFragment : Boolean, fragment: Fragment)

    fun setProfile( url : String)

}