package com.cherry.home.ui.scenario.automation.control

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean

interface AControlView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)

    fun setCondition(condition: SceneCondition, taskListBean: TaskListBean, value : String)

    fun setAction(action: SceneTask, taskListBean: TaskListBean, value : String)

    fun updateControl(task : MutableList<String>, requestCode : Int)
}