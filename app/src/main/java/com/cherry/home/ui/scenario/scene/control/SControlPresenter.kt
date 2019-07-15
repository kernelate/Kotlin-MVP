package com.cherry.home.ui.scenario.scene.control

import android.util.Log
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import javax.inject.Inject
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean


@ConfigPersistent
class SControlPresenter @Inject constructor() : BasePresenter<SControlView>() {

    private val TAG : String = "SControlPresenter"

    lateinit var devId : String
    lateinit var task: SceneTask

    private lateinit var taskListBean: TaskListBean

    companion object {
        internal val DP_ID : Long = 1009
    }

    fun init(devId: String, taskListBean: TaskListBean){
        checkViewAttached()
        this.devId = devId
        this.taskListBean = taskListBean

    }


    fun setControls(control : String) {
        val taskMap = HashMap<String, Any>()
        for (key in taskListBean.tasks){
            if(key.value == control){
                taskMap[taskListBean.dpId.toString()] = key.key //Turn on the device
            }
        }
        mvpView?.setTask(TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap), taskListBean, control)
    }

    fun setValue(countDown : Int){
        val taskMap = HashMap<String, Any>()
        taskMap[taskListBean.dpId.toString()] = countDown //Turn on the device
        mvpView?.setTask(TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap), taskListBean, countDown.toString())
        Log.d(TAG, "taskMap $taskMap taskListBean $taskListBean string $String countDown $countDown")
    }

    fun getTask() {
        var task : MutableList<String> = ArrayList()
        taskListBean.tasks.forEach {
            task.add(it.value)
        }
        mvpView?.updateControl(task)
    }

    fun getTaskType() : String = taskListBean.type

    fun getMaxTaskValue() : Int = taskListBean.valueSchemaBean.max

}