package com.cherry.home.ui.scenario.scene.function

import android.util.Log
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class SFunctionPresenter @Inject constructor() : BasePresenter<SFunctionView>() {

    private lateinit var deviceBean: DeviceBean

    lateinit var devId: String

    companion object {
        internal val DEV_ID : String = "devId"
        internal val DP_ID : String = "dpId"
        internal val DP_TYPE : String = "dpType"
    }


    fun getFunction(devId : String){
        checkViewAttached()
        deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)

        TuyaHomeSdk.getSceneManagerInstance().getDeviceTaskOperationList(devId, object : ITuyaResultCallback<List<TaskListBean>>{
            override fun onSuccess(task: List<TaskListBean>) {
                task.forEach {
                    Log.d("funcpres", "task devId ${it.id} name ${it.name} dpId ${it.dpId} id ${it.id} tasks ${it.tasks} valueSchemaBean ${it.valueSchemaBean.min} | ${it.valueSchemaBean.max} entityType ${it.entityType}  type ${it.type} devID $devId")
                }
                mvpView?.updateDevice(task)
            }

            override fun onError(code: String, error: String) {

            }

        })
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            item.forEach { s, schemaBean ->
//                val dps = Function(schemaBean.id, schemaBean.code, schemaBean.name, schemaBean.schemaType)
//                list.add(dps)
//            }
//        } else {
//            for((s, schemaDps) in item){
//                val dps = Function(schemaDps.id, schemaDps.code, schemaDps.name, schemaDps.schemaType)
//                list.add(dps)
//                Log.d("funcpres", "getSchemaMap ID = ${schemaDps.id} || Code = ${schemaDps.code} || Name = ${schemaDps.name} || schemaType = ${schemaDps.schemaType}")
//            }
//        }
//        list.sortBy { it.id }
//                Log.d("funcpres", "listsort $list")
//        mvpView?.updateDevice(list)
    }

}