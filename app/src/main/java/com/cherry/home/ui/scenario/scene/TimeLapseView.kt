package com.cherry.home.ui.scenario.scene

import com.cherry.home.ui.base.MvpView

interface TimeLapseView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)
}