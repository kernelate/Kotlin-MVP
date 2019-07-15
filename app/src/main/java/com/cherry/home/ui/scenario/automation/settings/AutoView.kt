package com.cherry.home.ui.scenario.automation.settings

import com.cherry.home.ui.base.MvpView
import com.cherry.home.ui.scenario.model.DeviceScene

interface AutoView : MvpView {

    fun showError(error_msg : String)

    fun setImage(image : String)

    fun setAutomationTask(auto: MutableList<DeviceScene>)

    fun setActionTask(scene: MutableList<DeviceScene>)

    fun returnSceneFragment(status: String)

    fun setScenarioName(name : String)

    fun setConditionType(type : Int)

    fun errToastCondition()

}