package com.cherry.home.ui.family.model

import android.content.Context
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback

interface IAddFamilyModel {
    fun createHome(context: Context, homeName: String, lon : Double, lat : Double, geoName : String?, roomList: List<String>, callback: ITuyaHomeResultCallback)
}