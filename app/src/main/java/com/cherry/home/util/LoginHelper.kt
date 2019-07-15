package com.cherry.home.util

import android.app.Activity
import android.content.Context
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.login.LoginActivity
import com.tuya.smart.home.sdk.TuyaHomeSdk

object LoginHelper {
    fun afterLogin() {

    }

    fun reLogin(context: Context) {
        reLogin(context, true)
    }

    fun reLogin(context: Context, tip: Boolean) {
        onLogout(context)
        if (tip) {

        }
        ActivityUtils.gotoActivity(context as Activity, LoginActivity::class.java, ActivityUtils.ANIMATE_FORWARD, true)
    }

    private fun onLogout(context: Context) {
        exit(context)
    }

    fun exit(context: Context) {
        Constant.finishActivity()
        TuyaHomeSdk.onDestroy()
    }

}