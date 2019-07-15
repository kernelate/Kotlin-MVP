package com.cherry.home.data.local

import com.tuya.smart.home.sdk.bean.HomeBean

interface PreferenceHelper {

    fun getPrefHomeID() : Long

    fun getPrefUID() : String

    fun getPrefMode() : Int

    fun putCurrentHome(homeBean: HomeBean)

    fun getCurrentHome() : HomeBean?

}