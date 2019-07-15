package com.cherry.home.ui.signup.forgotpass

import com.cherry.home.ui.base.MvpView

interface ForgotPasswordView : MvpView {

    fun showProgress(show : Boolean)

    fun showError(error_msg : String)

    fun showError(error : Throwable)

    fun gotoNext()
}