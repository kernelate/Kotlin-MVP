package com.cherry.home.ui.scenario.automation.conditionaction

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
class AConActionPresenter @Inject constructor() : BasePresenter<AConActionView>() {

    companion object {
        private val TAG : String = "AConActionPresenter"
    }

    fun init() {
        checkViewAttached()
        TuyaSdk.getEventBus().register(this)
        Constant.HOME_ID = PreferencesUtil.getLong("homeId", Constant.HOME_ID);
    }

    fun onEventMainThread(eventModel: DeviceUpdateEventModel){
        getData()
    }

    private fun getData(){
        getDataFromServer()
    }

    fun getDataFromServer() {

        TuyaHomeSdk.getSceneManagerInstance().getConditionDevList(Constant.HOME_ID, object : ITuyaResultCallback<List<DeviceBean>> {
            override fun onSuccess(deviceBean: List<DeviceBean>) {
                updateDeviceData(deviceBean)
            }

            override fun onError(p0: String?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }

    private fun updateDeviceData(list: List<DeviceBean>) {
        if (list.isNullOrEmpty()) {
            getDataFromServer()
        }
        else {
            mvpView?.updateDeviceData(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TuyaSdk.getEventBus().unregister(this)
    }

}