package com.cherry.home.ui.signup

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.android.mvp.bean.Result

interface SignupView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(result: Result)

    fun enableRegisterBtn(enable : Boolean)

    fun clearPassword()

    fun gotoLogin()


    fun getAccount() : String
}