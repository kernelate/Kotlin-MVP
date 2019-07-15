package com.cherry.home.ui.signup.forgotpass.validate

import com.cherry.home.ui.base.MvpView

interface CodeValidationView : MvpView{

    fun showProgress(show : Boolean)

    fun showError(error_msg : String)

    fun showError(error : Throwable)

    fun getClientId() : String

    fun getSecret() : String

    fun getURL() : String

    fun gotoNext()
}