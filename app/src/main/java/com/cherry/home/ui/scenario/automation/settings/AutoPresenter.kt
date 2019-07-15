package com.cherry.home.ui.scenario.automation.settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.scenario.model.DeviceScene
import com.cherry.home.ui.scenario.scene.settings.SSettingsPresenter
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject
import kotlin.random.Random

@ConfigPersistent
class AutoPresenter @Inject constructor(private val preferenceHelper: AppPreferenceHelper) : BasePresenter<AutoView>() {

    private val TAG : String = "AutoPresenter"

    private var sceneAutomationList = ArrayList<SceneCondition>()
    private var sceneActionList = ArrayList<SceneTask>()
    private var actionDeviceList = ArrayList<DeviceScene>()
    private var automationDeviceList = ArrayList<DeviceScene>()

    private var automationActionSize : Int = 0

    private lateinit var automationBackground : String
    private lateinit var deviceScene: DeviceScene
    private lateinit var deviceBean : DeviceBean

    private var automationId : String? = null

    companion object {
        internal val REQUEST_SCENE_BG = 3
        internal val ANY_CONDITION : String = "Any conditions is met"
        internal val ALL_CONDITION : String = "All conditions are met"
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SSettingsPresenter.REQUEST_SCENE_BG -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val image = data.getStringExtra("image")
                    preferenceHelper.sceneCover = image
                    mvpView?.setImage(image)
                }
            }
            Constant.REQUEST_SCENE_AUTO ->{
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.d(TAG, " resultcode $resultCode data $data")
                    var sceneAuto = data.getSerializableExtra("condition") as SceneCondition
                    val tasklistBean = data.getSerializableExtra("tasklist") as TaskListBean
                    val value = data.getStringExtra("value")

                    Log.d(TAG, "sceneAuto $sceneAuto tasklistBean $tasklistBean value $value")
                    deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(sceneAuto.entityId)
                    deviceScene = DeviceScene(null,sceneAuto.entityName, deviceBean.isOnline, tasklistBean.name, value, deviceBean.iconUrl, "bool")
                    automationDeviceList.add(deviceScene)
                    mvpView?.setAutomationTask(automationDeviceList)

                    sceneAutomationList.add(sceneAuto)
                }
            }

            Constant.REQUEST_SCENE_TASK ->{
                if (resultCode == Activity.RESULT_OK && data != null) {
                    var sceneTask = data.getSerializableExtra("task") as SceneTask
                    val tasklistBean = data.getSerializableExtra("tasklist") as TaskListBean
                    val value = data.getStringExtra("value")

                    deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(sceneTask.entityId)

                    deviceScene = if(!tasklistBean.tasks.isEmpty()){
                        DeviceScene(null,sceneTask.entityName, deviceBean.isOnline, tasklistBean.name, value, deviceBean.iconUrl, "bool")
                    } else {
                        DeviceScene(null,sceneTask.entityName, deviceBean.isOnline, tasklistBean.name, value, deviceBean.iconUrl, "value")
                    }
                    actionDeviceList.add(deviceScene)
                    mvpView?.setActionTask(actionDeviceList)

                    sceneActionList.add(sceneTask)
//                    mvpView?.setActionTask(sceneActionList)
                }
            }
        }
    }

    fun getAutoCover(){
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

    fun randomPick(image : String){
        automationBackground = image
        mvpView?.setImage(image)
    }

    fun saveAuto(sceneName : String, condition : String){
        val conditionType = getMatchType(condition)
        var homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        TuyaHomeSdk.getSceneManagerInstance().createScene(homeId, sceneName, automationBackground,
                sceneAutomationList, sceneActionList, conditionType, object : ITuyaResultCallback<SceneBean> {
            override fun onSuccess(sceneBean: SceneBean?) {
                mvpView?.returnSceneFragment("Save")
            }

            override fun onError(code: String, msg: String) {
                mvpView?.errToastCondition()
            }
        })
    }


    fun saveModifyAuto(sceneBean: SceneBean, sceneName: String, condition : String){
        val type = getMatchType(condition)
        sceneBean?.apply {
            name = sceneName
            actions = sceneActionList
            conditions = sceneAutomationList
            background = automationBackground
            matchType = type
        }
        TuyaHomeSdk.newSceneInstance(automationId).modifyScene(sceneBean, object  : ITuyaResultCallback<SceneBean> {
            override fun onSuccess(p0: SceneBean?) {
                Log.d(TAG, "onSuccessonSuccess 111")
                mvpView?.returnSceneFragment("Save")
            }

            override fun onError(code: String, error: String) {
                Log.d(TAG, "onSuccessonSuccess 222")
                mvpView?.showError(error)
            }
        })
    }

    fun getAutoListSize() : Int = sceneAutomationList.size

    fun getActionListSize() : Int = sceneActionList.size

    fun getSceneBeanActionSize() : Int = automationActionSize


    fun modifyAction(sceneBean: SceneBean){
        automationId = sceneBean.id
        if(sceneBean.actions != null){
            automationActionSize = sceneBean.actions.size
            sceneBean.actions.forEach {
                sceneActionList.add(it)
                deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(it.entityId)
                for(control in it.actionDisplayNew){
                    Log.d(TAG, "key ${control.key} value ${control.value[0]} ${control.value[1]}  it $it $")
                    deviceScene = DeviceScene(it.id,it.entityName, deviceBean.isOnline, control.value[0], control.value[1], deviceBean.iconUrl, "bool")
                }
                actionDeviceList.add(deviceScene)
            }

            sceneBean.conditions.forEach {
                sceneAutomationList.add(it)
                deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(it.entityId)
                Log.d(TAG, "it ${sceneBean.id} ${it.extraInfo} ${it.expr} ${it.exprDisplay}")
                deviceScene = DeviceScene(it.id, it.entityName, null, it.exprDisplay, null, null, "modified")
                automationDeviceList.add(deviceScene)
            }
        }
        automationBackground = sceneBean.background

        mvpView?.apply {
            setScenarioName(sceneBean.name)
            setConditionType(sceneBean.matchType)
            mvpView?.setActionTask(actionDeviceList)
            setAutomationTask(automationDeviceList)
            setImage(automationBackground)
        }
    }

    private fun getMatchType(type : String) : Int{
        when(type){
            AutoPresenter.ALL_CONDITION ->{
                return SceneBean.MATCH_TYPE_AND
            }
            AutoPresenter.ANY_CONDITION ->{
                return SceneBean.MATCH_TYPE_OR
            }
        }
        return SceneBean.MATCH_TYPE_OR
    }

    private fun deleteAuto(sceneBean: SceneBean) {
        automationId = sceneBean.id

        TuyaHomeSdk.newSceneInstance(automationId).deleteScene(object  : IResultCallback {
            override fun onSuccess() {
                mvpView?.returnSceneFragment("Delete")
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun getConditionList(){
        var homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        var sceneList = ArrayList<SceneBean>()
        var conditionList = ArrayList<SceneBean>()
    }
}