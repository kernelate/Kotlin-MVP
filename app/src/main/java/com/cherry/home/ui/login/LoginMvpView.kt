package com.cherry.home.ui.login

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.android.mvp.bean.Result
import com.tuya.smart.android.user.bean.User

interface LoginMvpView : MvpView {

    fun showProgress(show: Boolean)

    fun showError(error: Throwable)

    fun showError(error_msg: String)

    fun clearPassword()

    fun showDialog()

    fun openMainActivity()

    fun gotoSignupActivity()

    fun loginResult(what: Int, result: Result?)


}