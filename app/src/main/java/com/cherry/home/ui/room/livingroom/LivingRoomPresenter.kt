package com.cherry.home.ui.room.livingroom

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
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
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class LivingRoomPresenter @Inject constructor() : BasePresenter<LivingRoomView>(), DeviceUpdateEvent {

    private val TAG : String = "LivingRoomPresenter"

    fun init(){
        TuyaSdk.getEventBus().register(this)
    }

    fun getRoomDevices(){
        var homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback{
            override fun onSuccess(bean: HomeBean?) {
                bean?.rooms!!.forEach {
                    if(it.name == Constant.LIVING_ROOM){
                        mvpView?.setData(it.deviceList)
                        Log.d(TAG, "aaaa devicelist ${it.deviceList}")
                    }
                }
            }

            override fun onError(errorCode: String?, errorMsg: String?) {
                Log.d(TAG, "onError : $errorCode, $errorMsg")
            }

        })
    }

    override fun onEventMainThread(event: DeviceUpdateEventModel?) {
        getRoomDevices()
    }
}