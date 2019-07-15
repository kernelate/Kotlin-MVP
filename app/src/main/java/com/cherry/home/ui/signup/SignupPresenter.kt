package com.cherry.home.ui.signup

import android.os.Message
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.DataManager
import com.cherry.home.data.model.ServerRequest
import com.cherry.home.data.model.ServerResponse
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.MessageUtil
import com.cherry.home.util.rx.scheduler.SchedulerUtils
import com.tuya.smart.android.mvp.bean.Result
import javax.inject.Inject


class SignupPresenter @Inject constructor(private val dataManager: DataManager) : BasePresenter<SignupView>() {

    val REQUEST_CONFIRM = 999

    val MSG_REGISTER_SUCCESS = 15
    val MSG_REGISTER_FAILURE = 16

    val REQUEST_NO_PARAM = 100
    val REQUEST_NOT_EXIST = 101
    val REQUEST_UNKOWN_ERROR = 102
    val REQUEST_CORRECT = 105

    private var isRegistrationSuccess: Boolean = false

    fun registerUser(email: String, password: String) {
        checkViewAttached()
        getHandler()

//        if (registerAccountWithTuya(email, password) && registerAccountWithOrbivo(email, password)) {
//        dataManager.registerUser(email, password)
        dataManager.setMode(Constant.MODE_REGISTER, email, password)
                .compose<ServerResponse>(SchedulerUtils.ioToMain<ServerResponse>())
                .subscribe({ serverResponse ->
                    when (serverResponse.code) {
                        REQUEST_NO_PARAM -> {
                            val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, serverResponse.code.toString(), serverResponse.message)
                            mHandler?.sendMessage(msg)
                        }
                        REQUEST_NOT_EXIST -> {
                            val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, serverResponse.code.toString(), serverResponse.message)
                            mHandler?.sendMessage(msg)
                        }
                        REQUEST_UNKOWN_ERROR -> {
                            val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, serverResponse.code.toString(), serverResponse.message)
                            mHandler?.sendMessage(msg)
                        }
                        REQUEST_CORRECT -> {
                            mHandler?.sendEmptyMessage(MSG_REGISTER_SUCCESS)
                        }
                    }
                }) { err ->
                    mvpView?.apply {
                        showProgress(false)
                        showError(err)
                        Log.d("TAG", "I'M HERER: " + err.toString())
                    }
                }
    }

    override fun handleMessage(msg: Message): Boolean {
        mvpView?.apply {
            when (msg.what) {
                MSG_REGISTER_SUCCESS -> {
                    showProgress(false)
                    gotoLogin()
                }
                MSG_REGISTER_FAILURE -> {
                    showProgress(false)
                    clearPassword()
                    showError(msg.obj as Result)
                }
            }
        }

        return super.handleMessage(msg)
    }

//    private fun registerAccountWithTuya(email: String, password: String): Boolean {
//        TuyaHomeSdk.getUserInstance().registerAccountWithUid("63", email, password, mIRegisterCallBack)
//        return getRegistrationStatus()
//    }
//
//    private fun registerAccountWithOrbivo(email: String, password: String): Boolean {
//        UserApi.registerByEmail(email, MD5.encryptMD5(password), mBaseResultListener)
//        return getRegistrationStatus()
//    }

//    private val mBaseResultListener = BaseResultListener.DataListener { baseEvent, data ->
//        if (baseEvent.result == 0) {
//            setRegistrationStatus(true)
//        } else {
//            setRegistrationStatus(false)
//            val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, "30", "Unknown error in sdk")
//            mHandler?.sendMessage(msg)
//        }
//    }

//    private val mIRegisterCallBack = object : IRegisterCallback {
//        override fun onSuccess(user: User?) {
//            Log.d("TAG", "The UID registration succeeds.")
//            setRegistrationStatus(true)
//        }
//
//        override fun onError(code: String?, error_msg: String?) {
//            setRegistrationStatus(false)
//            val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, code.toString(), error_msg.toString())
//            mHandler?.sendMessage(msg)
//        }
//    }

//    private fun setRegistrationStatus(status: Boolean) {
//        isRegistrationSuccess = status
//    }
//
//    private fun getRegistrationStatus(): Boolean {
//        return isRegistrationSuccess
//    }
}