package com.cherry.home.ui.scenario.autosettings.timeperiod.repeat

import com.cherry.home.ui.base.MvpView

interface RepeatView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)
}