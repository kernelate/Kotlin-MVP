package com.cherry.home.ui.settings

import com.cherry.home.ui.base.MvpView

interface SettingsView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)
}