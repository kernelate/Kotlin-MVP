package com.cherry.home.ui.aboutus

import com.cherry.home.ui.base.MvpView

interface AboutUsView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)
}