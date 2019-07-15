package com.cherry.home.ui.scenario.scene.settings

import com.cherry.home.ui.base.MvpView
import com.cherry.home.ui.scenario.model.DeviceScene

interface SSettingsView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error_msg : String)

    fun setImage(image : String)

    fun setSceneTask(task : MutableList<DeviceScene>)

    fun setScenarioName(name : String)

    fun gotoSceneFragment()

    fun returnSceneFragment(status: String)

    fun errToast()


}