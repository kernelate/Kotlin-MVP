package com.cherry.home.ui.scenario

import android.transition.Scene
import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition

interface ScenarioView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)

    fun showNoSceneLayout(show : Boolean)

    fun getScenario(scenario : ArrayList<SceneBean>)

    fun getCondition(condition : List<SceneBean>)

    fun gotoSceneSettings(scenario: SceneBean)

    fun showToast()

    fun showScene()

    fun showAutomation()
}