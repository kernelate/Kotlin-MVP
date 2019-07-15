package com.cherry.home.ui.services

import com.cherry.home.ui.base.MvpView

interface ServicesView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)
}