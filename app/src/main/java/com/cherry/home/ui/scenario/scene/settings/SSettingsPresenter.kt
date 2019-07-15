package com.cherry.home.ui.scenario.scene.settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.scenario.model.DeviceScene
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject
import kotlin.random.Random



@ConfigPersistent
class SSettingsPresenter @Inject constructor(private val preferenceHelper: AppPreferenceHelper) : BasePresenter<SSettingsView>() {

    private val TAG : String = "SSettingsPresenter"

    private var taskList = ArrayList<SceneTask>()
    private var actionDeviceList = ArrayList<DeviceScene>()

    private var sceneActionSize : Int = 0

    private lateinit var sceneBackground : String
    private lateinit var deviceScene: DeviceScene
    private lateinit var deviceBean : DeviceBean

    private var sceneId : String? = null

    companion object {
        internal var REQUEST_SCENE_BG = 3
    }

    fun getSceneCover(){
        checkViewAttached()
        TuyaHomeSdk.getSceneManagerInstance().getSceneBgs(object : ITuyaResultCallback<ArrayList<String>> {
            override fun onSuccess(bg: ArrayList<String>) {
                val rnds = Random.nextInt(0, bg.size)
                val image = bg[rnds]
                randomPick(image)
            }

            override fun onError(code: String, msg: String) {
                mvpView?.showError(msg)
            }
        })
    }

    fun saveScene(sceneName : String){
        TuyaHomeSdk.getSceneManagerInstance().createScene(Constant.HOME_ID, sceneName, sceneBackground, null, taskList, 0, object : ITuyaResultCallback<SceneBean>{
            override fun onSuccess(sceneBean: SceneBean?) {
                Log.d(TAG, "savebtn saveScene")
                mvpView?.returnSceneFragment("Save")
            }

            override fun onError(code: String, msg: String) {
                mvpView?.errToast()
            }

        })
    }

    fun saveModifyScene(sceneBean: SceneBean,sceneName: String){
        sceneBean.apply {
            Log.d(TAG, "savebtn modifyscene")
            name = sceneName
            actions = taskList
            background = sceneBackground
        }

        TuyaHomeSdk.newSceneInstance(sceneId).modifyScene(sceneBean, object : ITuyaResultCallback<SceneBean>{
            override fun onSuccess(sceneBean: SceneBean) {
                Log.d(TAG, "Modify Scene Success")
                mvpView?.returnSceneFragment("Save")
            }

            override fun onError(code: String?, msg: String?) {
                Log.d(TAG,"code : $code msg : $msg")
            }

        })
    }

    fun getSceneBeanActionSize() : Int = sceneActionSize

    fun modifyScene(sceneBean: SceneBean){
        sceneId = sceneBean.id
        if(sceneBean.actions != null){
            sceneActionSize = sceneBean.actions.size
            sceneBean.actions.forEach {
                Log.d(TAG, "icon ${it.devIcon}")
                taskList.add(it)
                deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(it.entityId)
                for(control in it.actionDisplayNew){
                    Log.d(TAG, "key ${control.key} value ${control.value[0]} ${control.value[1]}  it $it $")
                    deviceScene = DeviceScene(it.id,it.entityName, deviceBean.isOnline, control.value[0], control.value[1], deviceBean.iconUrl, "bool")
                }
                actionDeviceList.add(deviceScene)
            }
        }
        sceneBackground = sceneBean.background

        mvpView?.apply {
            setScenarioName(sceneBean.name)
            setSceneTask(actionDeviceList)
            setImage(sceneBackground)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SCENE_BG -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    sceneBackground = data.getStringExtra("image")
                    mvpView?.setImage(sceneBackground)
                }
            }
            Constant.REQUEST_SCENE_TASK ->{
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.d(TAG,"REQUEST_SCENE_TASK  resultcode $resultCode data $data")
                    val sceneTask = data.getSerializableExtra("task") as SceneTask
                    val tasklistBean = data.getSerializableExtra("tasklist") as TaskListBean
                    val value = data.getStringExtra("value")
                    Log.d(TAG,"REQUEST_SCENE_TASK: $sceneTask, tasklistBean $tasklistBean, value $value")

                    deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(sceneTask.entityId)

                    if(!tasklistBean.tasks.isEmpty()){
                        for (key in tasklistBean.tasks){
                            Log.d(TAG,"key $key")
                            if(value == key.value){
                                Log.d(TAG,"value $value")
                                deviceScene = DeviceScene(null,sceneTask.entityName, deviceBean.isOnline, tasklistBean.name, key.value, deviceBean.iconUrl, "bool")
                                actionDeviceList.add(deviceScene)
                                mvpView?.setSceneTask(actionDeviceList)
                            }
                        }
                    } else {
                        deviceScene = DeviceScene(null,sceneTask.entityName, deviceBean.isOnline, tasklistBean.name, value, deviceBean.iconUrl, "value")
                        actionDeviceList.add(deviceScene)
                        mvpView?.setSceneTask(actionDeviceList)
                    }

                    taskList.add(sceneTask)
//                    actionDeviceList.add(deviceScene)
//                    mvpView?.setSceneTask(actionDeviceList)
                }
            }
        }
    }

    fun deleteScene(sceneBean: SceneBean){
        sceneId = sceneBean.id
        TuyaHomeSdk.newSceneInstance(sceneId).deleteScene(object : IResultCallback {
            override fun onSuccess() {
                Log.d(TAG, "Delete Scene Success")
                mvpView?.returnSceneFragment("Delete")
            }

            override fun onError(errorCode: String, errorMessage: String) {
                Log.d(TAG, "Delete Scene onError ")
            }

        })

    }

    private fun randomPick(image : String){
        Log.d(TAG, "image : $image")
        sceneBackground = image
        mvpView?.setImage(image)
    }
}