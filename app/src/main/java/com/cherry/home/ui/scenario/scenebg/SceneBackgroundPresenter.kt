package com.cherry.home.ui.scenario.scenebg

import android.util.Log
import com.cherry.home.ui.base.BasePresenter
import javax.inject.Inject
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback
import com.tuya.smart.home.sdk.TuyaHomeSdk



class SceneBackgroundPresenter @Inject constructor() :BasePresenter<SceneBackgroundView>() {

    private val TAG: String = "SceneBgPresenter"


    fun getSceneBackgroundList(){
        checkViewAttached()
        TuyaHomeSdk.getSceneManagerInstance().getSceneBgs(object : ITuyaResultCallback<ArrayList<String>> {
            override fun onSuccess(bg: ArrayList<String>) {
                mvpView?.updateImageView(bg)
            }

            override fun onError(code: String, msg: String) {
                mvpView?.showError(msg)
            }
        })
    }
}