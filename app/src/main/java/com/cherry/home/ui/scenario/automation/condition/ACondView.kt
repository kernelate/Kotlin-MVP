package com.cherry.home.ui.scenario.automation.condition

import com.cherry.home.ui.base.MvpView

interface ACondView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error : String)
}