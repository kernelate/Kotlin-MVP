package com.cherry.home.ui.scenario.scene.action

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.interior.event.DeviceUpdateEventModel
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class SActionPresenter @Inject constructor() : BasePresenter<SActionView>() {

    companion object {
        val TAG : String = "SActionPresenter"
    }


    fun init(){
        checkViewAttached()
        TuyaSdk.getEventBus().register(this)
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
    }

    fun onEventMainThread(eventModel: DeviceUpdateEventModel) {
        getData()
    }

    private fun getData()
    {
//        mvpView?.showProgress(true)
        getDataFromServer()
    }

    fun getDataFromServer() {

        TuyaHomeSdk.getSceneManagerInstance().getTaskDevList(Constant.HOME_ID, object : ITuyaResultCallback<List<DeviceBean>>{
            override fun onSuccess(deviceBean: List<DeviceBean>) {
                updateDeviceData(deviceBean)
            }

            override fun onError(code: String, error: String) {
                Log.d(TAG, "code: $code || error: $error")
            }

        })
//        mvpView?.showProgress(true)
//        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
//            override fun onSuccess(homeBeans: List<HomeBean>) {
//                if (homeBeans.isEmpty()) {
//                    mvpView?.apply {
////                        showProgress(false)
//                    }
//                }
//                val homeId = homeBeans[0].homeId
//                Constant.HOME_ID = homeId
//                PreferencesUtil.set("homeId", Constant.HOME_ID)
//                TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
//                    override fun onSuccess(bean: HomeBean) {
//                        Log.d(TAG, " getDataFromServer onsuccess ${Constant.HOME_ID}" )
////                        mvpView?.showProgress(false)
//                        updateDeviceData(bean.deviceList)
//                    }
//                    override fun onError(errorCode: String, errorMsg: String) {
//                        Log.d(TAG, " getDataFromServer onerror")
//                        mvpView?.apply {
////                            showProgress(false)
////                            showError(errorMsg)
//                        }
//                    }
//                })
//                TuyaHomeSdk.newHomeInstance(homeId).registerHomeStatusListener(object : ITuyaHomeStatusListener {
//                    override fun onDeviceAdded(devId: String) {
//                        Log.d(TAG, "device : $devId")
//                    }
//
//                    override fun onDeviceRemoved(devId: String) {
//
//                    }
//
//                    override fun onGroupAdded(groupId: Long) {
//
//                    }
//
//                    override fun onGroupRemoved(groupId: Long) {
//
//                    }
//
//                    override fun onMeshAdded(meshId: String) {
//                    }
//
//
//                })
//
//            }
//
//            override fun onError(errorCode: String, error: String) {
//                TuyaHomeSdk.newHomeInstance(Constant.HOME_ID).getHomeLocalCache(object : ITuyaHomeResultCallback {
//                    override fun onSuccess(bean: HomeBean) {
//                        Log.d(TAG, " getDataFromServer onsuccess2")
//                        updateDeviceData(bean.deviceList)
//                    }
//
//                    override fun onError(errorCode: String, errorMsg: String) {
//                        Log.d(TAG, " getDataFromServer onerror2")
//                        mvpView?.apply {
////                            showProgress(false)
////                            showError(errorMsg)
//                        }
//                    }
//                })
//            }
//        })
    }

    private fun updateDeviceData(list: List<DeviceBean>) {
            if (list.isEmpty()) {
                Log.d(TAG, " updateDeviceData if ")
//                showProgress(false)
//                showBackgroundView()
                getDataFromServer()
            } else {
                Log.d(TAG, " updateDeviceData else ")
//                showProgress(false)
//                hideBackgroundView()
                mvpView?.updateDeviceData(list)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, " onDestroy")
        TuyaSdk.getEventBus().unregister(this)
//        TuyaHomeSdk.getSceneManagerInstance().onDestroy();
    }
}