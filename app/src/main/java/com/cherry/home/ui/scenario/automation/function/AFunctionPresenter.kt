package com.cherry.home.ui.scenario.automation.function

import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class AFunctionPresenter @Inject constructor() : BasePresenter<AFunctionView>() {

    private val TAG : String = "AFunctionPresenter"

    private lateinit var deviceBean: DeviceBean

    lateinit var devId: String

    companion object {
        internal val DEV_ID : String = "devId"
    }

    fun getConditionFunction(devId : String){
        checkViewAttached()
        TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(devId, object : ITuyaResultCallback<List<TaskListBean>>{
            override fun onSuccess(task: List<TaskListBean>) {
                mvpView?.updateDevice(task)
            }

            override fun onError(p0: String?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    fun getActionFunction(devId: String){
        checkViewAttached()
        TuyaHomeSdk.getSceneManagerInstance().getDeviceTaskOperationList(devId, object : ITuyaResultCallback<List<TaskListBean>>{
            override fun onSuccess(task: List<TaskListBean>) {
                mvpView?.updateDevice(task)
            }

            override fun onError(p0: String?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}