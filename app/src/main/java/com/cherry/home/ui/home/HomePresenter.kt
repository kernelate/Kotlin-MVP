package com.cherry.home.ui.home

import android.util.Log
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.base.event.NetWorkStatusEvent
import com.tuya.smart.android.base.event.NetWorkStatusEventModel
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.android.panel.TuyaPanelSDK
import com.tuya.smart.android.panel.api.ITuyaPanelLoadCallback
import com.tuya.smart.android.panel.utils.ProgressUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.api.ITuyaHomeStatusListener
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.interior.event.DeviceUpdateEvent
import com.tuya.smart.interior.event.DeviceUpdateEventModel
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.sdk.api.IRequestCallback
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject
import com.tuya.smart.android.panel.api.ITuyaOpenUrlListener
import com.tuya.smart.android.panel.api.ITuyaPressedRightMenuListener



@ConfigPersistent
class HomePresenter @Inject constructor(private val preferenceHelper: AppPreferenceHelper) : BasePresenter<HomeMvpView>(), DeviceUpdateEvent {

    private val TAG : String = "HomePresenter"
    private val SMART_SOCKET_ID : String = "k0rPvM4kSzFeifWG"
    private val POWER_STRIP_ID : String = "GfBFx6R8qDXnueVF"
    private val WALL_SWITCH_ID : String = "jBtt1AX7VC5KDQyT"
    private val WALL_SOCKET_ID : String = "EgYFEdbmwr8jeUUY"
    private val DOOR_WINDOW_SENSOR_ID : String = "IMLyos48UBeNOKCP"
    companion object {
        val TAG: String = "HomePresenter"
    }

    fun init(){
        TuyaSdk.getEventBus().register(this)
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID);
    }

    override fun onEventMainThread(eventModel: DeviceUpdateEventModel) {
        getDataFromServer()
    }

    fun getDataFromServer() {
        mvpView?.showProgress(true)
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(homeBeans: List<HomeBean>) {
                if (homeBeans.isEmpty()) {
                    mvpView?.apply {
                        showProgress(false)
                    }
                    return
                }
                val homeId = homeBeans[0].homeId
                Constant.HOME_ID = homeId
                PreferencesUtil.set("homeId", Constant.HOME_ID)
                TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
                    override fun onSuccess(bean: HomeBean) {
                        mvpView?.showProgress(false)
                        updateDeviceData(bean.deviceList)
                    }
                    override fun onError(errorCode: String, errorMsg: String) {
                        mvpView?.apply {
                            showProgress(false)
                            showError(errorMsg)
                        }
                    }
                })
                TuyaHomeSdk.newHomeInstance(homeId).registerHomeStatusListener(object : ITuyaHomeStatusListener {
                    override fun onDeviceAdded(devId: String) {

                    }

                    override fun onDeviceRemoved(devId: String) {

                    }

                    override fun onGroupAdded(groupId: Long) {

                    }

                    override fun onGroupRemoved(groupId: Long) {

                    }

                    override fun onMeshAdded(meshId: String) {
                    }


                })

            }

            override fun onError(errorCode: String, error: String) {
                TuyaHomeSdk.newHomeInstance(Constant.HOME_ID).getHomeLocalCache(object : ITuyaHomeResultCallback {
                    override fun onSuccess(bean: HomeBean) {
                        mvpView?.showProgress(false)
                        updateDeviceData(bean.deviceList)
                    }

                    override fun onError(errorCode: String, errorMsg: String) {
                        mvpView?.apply {
                            showProgress(false)
                            showError(errorMsg)
                        }
                    }
                })
            }
        })
    }


    fun addDevice() {
        TuyaHomeSdk.getRequestInstance().requestWithApiName("s.m.dev.sdk.demo.list", "1.0", null, object : IRequestCallback {
            override fun onSuccess(result: Any) {
                mvpView?.showProgress(false)
                getDataFromServer()
            }

            override fun onFailure(errorCode: String, errorMsg: String) {
                mvpView?.apply {
                    showProgress(false)
                    showError(errorMsg)
                }
            }
        })
    }

    private fun updateDeviceData(list: List<DeviceBean>) {
        mvpView?.apply {
            if (list.isEmpty()) {
                showProgress(false)
                showBackgroundView()
            } else {
                showProgress(false)
                hideBackgroundView()
                updateDeviceData(list)
            }
        }
    }

    private fun unBindDevice(deviceBean: DeviceBean) {
        TuyaHomeSdk.newDeviceInstance(deviceBean.getDevId()).removeDevice(object : IResultCallback {
            override fun onError(s: String, s1: String) {
                mvpView?.showProgress(false)
            }

            override fun onSuccess() {
                mvpView?.showProgress(false)
            }
        })
    }


    fun onClickDevice(bean : DeviceBean){
        Log.d(TAG,"111schema ${bean.devId} || ${bean.getSchema()} || ${bean.schemaMap} ||  ${bean.name} ")

        panelController(bean.getDevId())
    }

    private fun panelController(devId : String){
        mvpView?.gotoPanelViewController(Constant.HOME_ID, devId, object : ITuyaPanelLoadCallback{
            override fun onSuccess() {
                mvpView?.hideProgress()
            }

            override fun onProgress(p0: Int) {

            }

            override fun onError(code: Int, error: String) {
                Log.d(TAG,"code $code error $error")
                mvpView?.hideProgress()

            }

            override fun onStart() {
                mvpView?.showProgress()
            }
        })

        TuyaPanelSDK.getPanelInstance().setPressedRightMenuListener {
            mvpView?.gotoDeviceSetting(devId)
        }


        TuyaPanelSDK.getPanelInstance().setOpenUrlListener{
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        TuyaSdk.getEventBus().unregister(this)
        TuyaPanelSDK.getPanelInstance().clearPanelCache();
    }


}


