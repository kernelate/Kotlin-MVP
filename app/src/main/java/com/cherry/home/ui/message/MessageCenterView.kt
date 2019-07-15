package com.cherry.home.ui.message

import com.cherry.home.ui.base.MvpView

interface MessageCenterView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)
}