package com.cherry.home.ui.family.model

import android.content.Context
import com.cherry.home.ui.family.FamilyManager
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback

object AddFamilyModel : IAddFamilyModel {

    override fun createHome(context: Context, homeName: String, lon : Double, lat : Double, geoName : String?, roomList: List<String>, callback: ITuyaHomeResultCallback) {
        FamilyManager.getInstance(context).createHome(homeName, lon, lat, geoName, roomList, callback)
    }

}