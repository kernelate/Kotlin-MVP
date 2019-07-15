package com.cherry.home.ui.scenario.automation.control

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import javax.inject.Inject
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.home.sdk.bean.scene.condition.ConditionListBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.sdk.api.ITuyaDataCallback
import com.tuya.smart.home.sdk.bean.scene.condition.property.BoolProperty






@ConfigPersistent
class AControlPresenter @Inject constructor() : BasePresenter<AControlView>() {

    private val TAG : String = "AControlPresenter"

    lateinit var devId : String
    lateinit var task: SceneTask

    private lateinit var taskListBean: TaskListBean
    private lateinit var deviceBean: DeviceBean
    private lateinit var conditionListBean: ConditionListBean

    private var requestCode : Int = -1

    companion object {
        internal val DP_ID : Long = 1009
    }

    fun init(devId: String, taskListBean: TaskListBean, requestCode : Int){
        checkViewAttached()
        this.devId = devId
        this.taskListBean = taskListBean
        this.requestCode = requestCode
        deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)
    }

    fun setControls(control : String) {
        when(requestCode){
            Constant.REQUEST_SCENE_TASK->{
                val taskMap = HashMap<String, Any>()
                for (key in taskListBean.tasks){
                    if(key.value == control){
                        taskMap[taskListBean.dpId.toString()] = key.key //Turn on the device
                    }
                }
                mvpView?.setAction(TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap), taskListBean, control)
            }

            Constant.REQUEST_SCENE_AUTO ->{
                if(getTaskType() == "bool"){
                    var value = false
                    value = control == "ON"

                    //When the device starts.
                    val boolRule = BoolRule.newInstance(
                            "dp"+taskListBean.dpId, //"dp" + dpId
                            value    //bool of triggering conditions
                    )
                    val devCondition = SceneCondition.createDevCondition(
                            deviceBean, //Device
                            taskListBean.dpId.toString(), //dpId
                            boolRule    //Rule
                    )

                    mvpView?.setCondition(devCondition, taskListBean, control)
                }
            }
        }
    }

    fun setValue(countDown : Int) {
        val taskMap = HashMap<String, Any>()
        taskMap[taskListBean.dpId.toString()] = countDown //Turn on the device
        mvpView?.setAction(TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap), taskListBean, countDown.toString())
        Log.d(TAG, "taskMap $taskMap taskListBean $taskListBean string $String countDown $countDown")
    }

    fun getTask() {
        var task : MutableList<String> = ArrayList()
        taskListBean.tasks.forEach {
            task.add(it.value)
        }
        mvpView?.updateControl(task, requestCode)
    }

    fun getTaskType() : String = taskListBean.type

    fun getMaxTaskList() : Int = taskListBean.valueSchemaBean.max
}