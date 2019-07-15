package com.cherry.home.ui.device.location

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.bean.RoomBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import javax.inject.Inject


@ConfigPersistent
class DeviceLocationPresenter @Inject constructor() : BasePresenter<DeviceLocationView>() {

    private var TAG : String = "DeviceLocationPresenter"

    private lateinit var roomBean: RoomBean

    init {
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
    }

    fun getHomeRoomList(){
        checkViewAttached()
        TuyaHomeSdk.newHomeInstance(Constant.HOME_ID).getHomeDetail(object : ITuyaHomeResultCallback{
            override fun onSuccess(homebean: HomeBean) {
                val roomList = homebean.rooms
                Log.d(TAG, "roomList size ${roomList.size}")
                if(roomList?.size != 0){
                    mvpView?.getRoomList(roomList.toMutableList())
                }
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })

    }

    fun setRoom(roomBean: RoomBean){
        this.roomBean = roomBean
    }

    fun getRoom() : RoomBean?{
        if(roomBean != null){
            return roomBean
        }
        return null
    }
}