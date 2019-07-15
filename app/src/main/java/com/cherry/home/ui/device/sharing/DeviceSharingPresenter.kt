package com.cherry.home.ui.device.sharing

import android.util.Log
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.SharedUserInfoBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import javax.inject.Inject

@ConfigPersistent
class DeviceSharingPresenter @Inject constructor() : BasePresenter<DeviceSharingView>(){

    private var TAG : String = "DeviceSharingPresenter"

    private lateinit var devId: String

    fun init(devId : String){
        this.devId = devId
    }

    fun getSharedUserList(){
        TuyaHomeSdk.getDeviceShareInstance().queryDevShareUserList(devId, object : ITuyaResultCallback<List<SharedUserInfoBean>>{
            override fun onSuccess(sharedUserInfoBean: List<SharedUserInfoBean>) {
                mvpView?.getSharedUserList(sharedUserInfoBean)
            }

            override fun onError(code: String?, error: String) {
                mvpView?.showError(error)
            }

        })
    }
}