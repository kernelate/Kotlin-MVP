package com.cherry.home.ui.scenario.automation.function

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean

interface AFunctionView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)

    fun updateDevice(function: List<TaskListBean>)
}