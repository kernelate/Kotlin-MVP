package com.cherry.home.ui.scenario.scene.function

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean

interface SFunctionView : MvpView {

    fun showError(error: Throwable)

    fun updateDevice(list : List<TaskListBean>)
}