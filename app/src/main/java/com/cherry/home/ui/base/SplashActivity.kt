package com.cherry.home.ui.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.cherry.home.ui.login.LoginActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.LoginHelper
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.common.utils.TuyaUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk

class SplashActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        L.d("splash", "tuyaTime: " + TuyaUtil.formatDate(System.currentTimeMillis(), "yyyy-mm-dd hh:mm:ss"))

        if (TuyaHomeSdk.getUserInstance().isLogin) {//已登录，跳主页
            LoginHelper.afterLogin()
            ActivityUtils.gotoMainActivity(this)
        } else {
            ActivityUtils.gotoActivity(this, LoginActivity::class.java, ActivityUtils.ANIMATE_FORWARD, true)
//            ActivityUtils.gotoMainActivity(this)
        }
    }
}