package com.cherry.home.ui.scenario.scene.control

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean

interface SControlView : MvpView {

    fun showError(error: Throwable)

    fun updateControl(task : MutableList<String>)

    fun setTask(task : SceneTask, taskListBean: TaskListBean, value : String)
}