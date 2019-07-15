package com.cherry.home.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Message
import android.util.Log
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.data.DataManager
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.data.model.ServerResponse
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.LoginHelper
import com.cherry.home.util.MessageUtil
import com.cherry.home.util.rx.scheduler.SchedulerUtils
import com.tuya.smart.android.mvp.bean.Result
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.sdk.TuyaSdk
import okhttp3.ResponseBody
import javax.inject.Inject


@ConfigPersistent
class LoginPresenter @Inject constructor(private val dataManager: DataManager, private val preferenceHelper: AppPreferenceHelper) : BasePresenter<LoginMvpView>() {


    internal val REQUEST_LOGIN = 10
    internal val MSG_LOGIN_SUCCESS = 15
    internal val MSG_LOGIN_FAILURE = 16

    internal val MSG_REGISTER_SUCCESS = 17
    internal val MSG_REGISTER_FAILURE = 18

    private val MSG_ACTIVATE_SUCCESS = 19
    internal val MSG_ACTIVATE_FAILURE = 20

    private val MSG_NEED_REGISTER = 2
    private val MSG_NEED_LOGIN = 3

    val MSG_ACCOUNT_EXIST = 1

    private val MSG_NOT_EXIST = "The email you've entered does not exist"

    private val REQUEST_NOT_EXIST = 21
    private val REQUEST_NO_PARAM = 20
    private val REQUEST_INCORRECT_ACCOUNT = 22
    private val REQUEST_CORRECT = 25


    private val ACCOUNT_VALIDATED = "1"
    private val ACCOUNT_INACTIVE = "0"
    private val ACCOUNT_ACTIVE = "3"

    private val REQUEST_ACCOUNT_EXIST = "IS_EXISTS"

    private val TAG: String = "LoginPresenter"

    lateinit var email : String
    lateinit var password : String

    fun login(email: String, password: String) {
        this.email = email
        this.password = password
        checkViewAttached()
        getHandler()
        dataManager.setMode(Constant.MODE_LOGIN,email, password)
                .compose<ServerResponse>(SchedulerUtils.ioToMain<ServerResponse>())
                .subscribe({ response ->
                    when (response.code) {
                        REQUEST_NOT_EXIST -> {
                            Log.d(TAG, "code: ${response.code} | message: ${response.message}")
                            val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, response.code.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }
                        REQUEST_CORRECT -> {
                            Log.d(TAG, "code: ${response.code} | message: ${response.message}")
                            val user = com.cherry.home.data.model.User(response.user.email, response.user.uid)

                            preferenceHelper.userName = user.email
                            when (response.status) {
                                ACCOUNT_VALIDATED -> {
                                    mHandler?.sendEmptyMessage(MSG_NEED_REGISTER)
                                }
                                ACCOUNT_INACTIVE -> {
                                    Log.d(TAG, "status: ${response.status}")
                                    val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, REQUEST_NOT_EXIST.toString(), MSG_NOT_EXIST)
                                    mHandler?.sendMessage(msg)
                                }
                                ACCOUNT_ACTIVE -> {
                                    mHandler?.sendEmptyMessage(MSG_NEED_LOGIN)
                                }
                            }
                        }
                        REQUEST_INCORRECT_ACCOUNT -> {
                            Log.d(TAG, "code: ${response.code} | message: ${response.message}")
                            val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, response.code.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }

                        REQUEST_NO_PARAM -> {
                            Log.d(TAG, "code: ${response.code} | message: ${response.message}")
                            val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, response.code.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }
                    }
                }) { throwable ->
                    mvpView?.apply {
                        val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, "300", throwable.toString())
                        mHandler?.sendMessage(msg)
//
                    }
                }

    }

    private fun loginWithTuya(email: String, password: String) {
        TuyaHomeSdk.getUserInstance().loginWithUid("63", email, password, object : ILoginCallback{
            override fun onSuccess(user: User?) {
                mHandler?.sendEmptyMessage(MSG_LOGIN_SUCCESS)
            }
            override fun onError(code: String?, msg: String?) {
                val msg = MessageUtil.getCallFailMessage(MSG_LOGIN_FAILURE, code.toString(), msg.toString())
                mHandler?.sendMessage(msg)
            }
        })
    }

    private fun registerAccountWithTuya(email: String, password: String) {
        TuyaHomeSdk.getUserInstance().registerAccountWithUid("63", email, password, object : IRegisterCallback{
            override fun onSuccess(user: User?) {
                mHandler?.sendEmptyMessage(MSG_REGISTER_SUCCESS)
            }

            override fun onError(code: String?, error_msg: String?) {
                when(code){
                    REQUEST_ACCOUNT_EXIST -> {
                        mHandler?.sendEmptyMessage(MSG_ACCOUNT_EXIST)
                    }
                    else -> {
                        val msg = MessageUtil.getCallFailMessage(MSG_REGISTER_FAILURE, code.toString(), error_msg.toString())
                        mHandler?.sendMessage(msg)
                    }
                }
            }

        })
    }


    override fun handleMessage(msg: Message): Boolean {
        mvpView?.apply {
            when (msg.what) {
                MSG_LOGIN_SUCCESS -> {
                    // 登录成功
                    loginResult(msg.what, null)
                    Constant.finishActivity()
                    LoginHelper.afterLogin()
                    openMainActivity()
                }
                MSG_REGISTER_SUCCESS, MSG_ACCOUNT_EXIST ->{
                    activate(email, password)
                }
                MSG_ACTIVATE_SUCCESS, MSG_NEED_LOGIN ->{
                    loginWithTuya(email, password)
                }
                MSG_NEED_REGISTER -> {
                    registerAccountWithTuya(email, password)
                }
                MSG_LOGIN_FAILURE, MSG_REGISTER_FAILURE, MSG_ACTIVATE_FAILURE ->{
                    showProgress(false)
                    loginResult(msg.what, msg.obj as Result)
                }
                else -> {
                }
            }
        }
        return super.handleMessage(msg)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    mvpView?.showDialog()
                } else {
                    mvpView?.clearPassword()
                }
            }
        }
    }

    private fun activate(email : String, password: String){
        dataManager.activate(Constant.MODE_ACTIVATE, email)
                .compose(SchedulerUtils.ioToMain<ResponseBody>())
                .subscribe({
                    mHandler?.sendEmptyMessage(MSG_ACTIVATE_SUCCESS)
                }){error ->
                    val msg = MessageUtil.getCallFailMessage(MSG_ACTIVATE_FAILURE, "1002", error.toString())
                    mHandler?.sendMessage(msg)
                }
    }

}