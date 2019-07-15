package com.cherry.home.ui.device.setting

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.ProgressUtil
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.api.ITuyaDevice
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class DeviceSettingPresenter @Inject constructor() : BasePresenter<DeviceSettingView>() {


    private val TAG: String = "DeviceSettingPresenter"
    private lateinit var iTuyaDevice: ITuyaDevice
    private lateinit var deviceBean: DeviceBean
    private lateinit var devId: String

    companion object {
        internal val REQUEST_LOCATION = 2
    }

    init {
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
    }

    fun initData(devId: String) {
        checkViewAttached()
        this.devId = devId

        iTuyaDevice = TuyaHomeSdk.newDeviceInstance(devId)
        deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)

        getDeviceName()
        getDeviceLocation()
    }

    fun getDeviceName() {
        mvpView?.setDeviceName(deviceBean.getName())
    }

    fun unBind(devId: String) {
        TuyaHomeSdk.newDeviceInstance(devId).removeDevice(object : IResultCallback {
            override fun onError(s: String, s1: String) {
                ProgressUtil.hideLoading()
            }

            override fun onSuccess() {
                ProgressUtil.hideLoading()
                mvpView?.gotoMainActivity()
            }
        })
    }

    fun renameDevice(titleName: String) {
        iTuyaDevice.renameDevice(titleName, object : IResultCallback {
            override fun onSuccess() {
                ProgressUtil.hideLoading()
                mvpView?.setDeviceName(titleName)
            }

            override fun onError(p0: String?, p1: String?) {
                ProgressUtil.hideLoading()
            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val name = data.getStringExtra("name")
                    val id = data.getLongExtra("id", 0)
                    addDeviceLocation(id, name)
                }
            }
        }
    }

    private fun addDeviceLocation(roomId: Long, name: String) {
        if (devId != null) {
            TuyaHomeSdk.newRoomInstance(roomId).addDevice(devId, object : IResultCallback {
                override fun onSuccess() {
                    mvpView?.setDeviceLocation(name)
                }

                override fun onError(code: String, msg: String) {
                    mvpView?.showError(code, msg)
                }

            })
        }
    }

    private fun getDeviceLocation(){
        val room = TuyaHomeSdk.getDataInstance().getDeviceRoomBean(devId)
        if(room != null){
            mvpView?.setDeviceLocation(room.name)
        }
    }
}