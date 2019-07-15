package com.cherry.home.ui.scenario

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.util.TimeUtils
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.base.utils.PreferencesUtil
import javax.inject.Inject
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.scene.PlaceFacadeBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.sdk.api.IDevListener
import com.tuya.smart.sdk.api.IResultCallback
import com.tuya.smart.sdk.bean.DeviceBean


@ConfigPersistent
class ScenarioPresenter @Inject constructor() : BasePresenter<ScenarioView>(), IDevListener{

    private val TAG : String = "ScenarioPresenter"

    private var sceneId : String? = null

    private lateinit var deviceBean: DeviceBean
    private lateinit var sceneList : ArrayList<SceneBean>
    private lateinit var conditionList : ArrayList<SceneBean>

    fun getSmartList(){
        checkViewAttached()
        var homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        mvpView?.showProgress(true)
        sceneList = ArrayList<SceneBean>()
        conditionList = ArrayList<SceneBean>()
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(homeId, object : ITuyaResultCallback<List<SceneBean>> {
            override fun onSuccess(result: List<SceneBean>) {
                if(result.isNullOrEmpty()){
                    mvpView?.apply {
                        showProgress(false)
                        showNoSceneLayout(true)
                    }

                } else {
                    mvpView?.showProgress(false)
                    result.forEach {
                        if(it.conditions != null){
                            conditionList.add(it)
                        } else {
                            sceneList.add(it)
                        }
                    }
                    mvpView?.apply {
                        showNoSceneLayout(false)
                        getScenario(sceneList)
                        getCondition(conditionList)
                        showScene()
                        showAutomation()
                    }
                }
            }
            override fun onError(errorCode: String, errorMessage: String) {
                mvpView?.apply {
                    showProgress(false)
                    showError(errorMessage)
                }
            }
        })
    }

    fun executeScene(sceneBean: SceneBean){
        sceneId = sceneBean.id
        var devId : String? = null

        if(sceneBean.actions != null){
            sceneBean.actions.forEach {
                devId = it.entityId
                TuyaHomeSdk.newDeviceInstance(devId).registerDevListener(this)
            }
            deviceBean = TuyaHomeSdk.getDataInstance().getDeviceBean(devId)
            if(deviceBean.isOnline){
                TuyaHomeSdk.newSceneInstance(sceneId).executeScene(object : IResultCallback {
                    override fun onSuccess() {
                    }

                    override fun onError(errorCode: String, errorMessage: String) {
                        Log.d(TAG, "code :$errorCode msg: $errorMessage")
                    }
                })
            } else {
                mvpView?.showToast()

            }

        } else {
            mvpView?.gotoSceneSettings(sceneBean)
        }
    }

    fun setAutomationEnable(sceneBean: SceneBean){
        Log.d(TAG,"enable : ${sceneBean.isEnabled} ${System.currentTimeMillis()}")
        TuyaHomeSdk.newSceneInstance(sceneBean.id).modifyScene(sceneBean, object  : ITuyaResultCallback<SceneBean> {
            override fun onSuccess(p0: SceneBean?) {
                Log.d(TAG, "Success")
                mvpView?.getCondition(conditionList)
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    override fun onDpUpdate(devId: String?, dps: String?) {

    }

    override fun onStatusChanged(devId: String?, status: Boolean) {

    }

    override fun onRemoved(devId: String?) {

    }

    override fun onDevInfoUpdate(devId: String?) {

    }

    override fun onNetworkStatusChanged(devId: String?, status: Boolean) {

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            Constant.REQUEST_MODIFY_SCENETASK, Constant.REQUEST_ADD_SCENE ->{
                if(resultCode != Activity.RESULT_OK){
                    getSmartList()
                }
                if(resultCode == Activity.RESULT_OK && data != null){
                    val status = data.getStringExtra("status")
                    getSmartList()
                }
            }
        }
    }

}