package com.cherry.home.ui.signup.forgotpass

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.DataManager
import com.cherry.home.data.model.ChangePasswordResponse
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.rx.scheduler.SchedulerUtils
import javax.inject.Inject

@ConfigPersistent
class ForgotPasswordPresenter @Inject constructor(private val dataManager: DataManager) : BasePresenter<ForgotPasswordView>() {

    private val TAG : String = "ForgotPasswordPresenter"

    private val INVALID_EMAIL = 120

    fun sendEmail(email : String){
        checkViewAttached()
        dataManager.getVerificationCode(Constant.MODE_FORGOT_PASSWORD, Constant.PROC_FOR_EMAIL, email)
                .compose<ChangePasswordResponse>(SchedulerUtils.ioToMain<ChangePasswordResponse>())
                .subscribe({ response ->
                    when(response.code){
                        INVALID_EMAIL ->{
                            mvpView?.apply {
                                showProgress(false)
                                showError(response.message)
                            }
                        }
                        else ->{
                            Log.d(TAG,"${response.message}")
                            mvpView?.apply {
                                showProgress(false)
                                gotoNext()
                            }
                        }
                    }
                }){ err ->
                    Log.d(TAG,"err ${err.toString()}")
                    mvpView?.apply {
                        showProgress(false)
                        showError(err)
                    }
                }

    }

}