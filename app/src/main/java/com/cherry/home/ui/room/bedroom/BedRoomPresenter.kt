package com.cherry.home.ui.room.bedroom

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.interior.event.DeviceUpdateEvent
import com.tuya.smart.interior.event.DeviceUpdateEventModel
import com.tuya.smart.sdk.TuyaSdk
import javax.inject.Inject


@ConfigPersistent
class BedRoomPresenter @Inject constructor() : BasePresenter<BedroomView>(), DeviceUpdateEvent {

    private val TAG : String = "BedRoomPresenter"

    fun init() {
        Log.d(TAG, "devices init ")
        TuyaSdk.getEventBus().register(this)
    }

    fun getRoomDevices(){
        Log.d(TAG, "getRoomDevices")
        var homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback{
            override fun onSuccess(bean: HomeBean?) {
                Log.d(TAG, "onsuccess")
                bean?.rooms!!.forEach {
                    Log.d(TAG, "aaaa devicelist ${it.deviceList} name ${it.name} ")
                    if(it.name == Constant.BED_ROOM){
                        mvpView?.setData(it.deviceList)
                        Log.d(TAG, "aaaa devicelist ${it.deviceList}")
                    }
                }
            }

            override fun onError(errorCode: String, errorMsg: String) {
                Log.d(TAG, " errorcode $errorCode errormsg $errorMsg")
            }

        })
    }

    override fun onEventMainThread(event : DeviceUpdateEventModel?){
        getRoomDevices()
    }
}