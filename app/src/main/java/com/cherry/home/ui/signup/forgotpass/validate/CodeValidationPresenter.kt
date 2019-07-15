package com.cherry.home.ui.signup.forgotpass.validate

import android.os.Message
import android.util.Half.getSign
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.DataManager
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.data.model.*
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.MessageUtil
import com.cherry.home.util.md5
import com.cherry.home.util.rx.scheduler.SchedulerUtils
import com.google.firebase.internal.FirebaseAppHelper.getToken
import com.tuya.smart.android.mvp.bean.Result
import java.util.*
import javax.inject.Inject


@ConfigPersistent
class CodeValidationPresenter @Inject constructor(private val dataManager: DataManager, private val preferenceHelper: AppPreferenceHelper) : BasePresenter<CodeValidationView>() {

    private val TAG: String = "CodeValidationPresenter"

    private val MSG_CHANGEPASS_SUCCESS = 10
    private val MSG_CHANGEPASS_FAILURE = 1

    private val MSG_CHANGEPASS_WITH_TUYA_SUCCESS = 20
    private val MSG_CHANGEPASS_WITH_TUYA_FAILURE = 2

    private val MSG_VERIFY_CODE_SUCCES = 3
    private val MSG_VERIFY_CODE_FAILURE = 30

    private val MSG_GET_TOKEN_SUCCESS = 99
    private val MSG_GET_TOKEN_FAILURE = 90

    private val MSG_REFRESH_TOKEN_SUCCESS = 68
    private val MSG_REFRESH_TOKEN_FAILURE = 60

    private val MSG_GET_SIGN_SUCCESS = 88
    private val MSG_GET_SIGN_FAILURE = 80

    private val MSG_SIGN_REFRESH_SUCCESS = 58
    private val MSG_SIGN_REFRESH_FAILURE = 50

    private val MSG_GET_SIGN_TOKEN_SUCCESS = 78
    private val MSG_GET_SIGN_TOKEN_FAILURE = 70

    private val REQUEST_NO_PARAM = 20

    private val TOKEN = 1
    private val REFRESH_TOKEN = 2

    internal val INTENT_EMAIL: String = "intent_email"

    private val INVALID_CODE: Int = 110
    private val CHANGE_PASS_SUCCESS : Int = 131
    private val CHANGE_PASS_FAILED : Int = 130

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var code : String

    private lateinit var url: String
    private lateinit var clientId: String
    private lateinit var secret: String
    private lateinit var signed: String
    private lateinit var signedToken: String

    private var token : String? = null
    private var refresh_token : String? = null

    private var timeStamp: Long? = null

    init {
        getHandler()
    }

    fun verifyCode(code: String, email: String, password: String){
        checkViewAttached()
        dataManager.sendValidationCode(Constant.MODE_FORGOT_PASSWORD, Constant.PROC_FOR_CODE, code, email)
                .compose<ChangePasswordResponse>(SchedulerUtils.ioToMain<ChangePasswordResponse>())
                .subscribe({ response ->
                    when(response.code){
                        INVALID_CODE -> {
                            val msg = MessageUtil.getCallFailMessage(MSG_VERIFY_CODE_FAILURE, INVALID_CODE.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }
                        else -> {
                            this.code = code
                            this.email = email
                            this.password = password
                            mHandler?.sendEmptyMessage(MSG_VERIFY_CODE_SUCCES)
                        }
                    }
                }) { error->
                    mvpView?.apply {
                        showProgress(false)
                        showError(error)
                    }
                }
    }

    override fun handleMessage(msg: Message): Boolean {
        mvpView?.apply {
            when (msg.what) {
                MSG_VERIFY_CODE_SUCCES -> {
                    getSign(TOKEN)
                }
                MSG_GET_SIGN_SUCCESS ->{
                    getToken()
                }
                MSG_GET_TOKEN_SUCCESS -> {
                    getSignToken()
                }
                MSG_GET_SIGN_TOKEN_SUCCESS ->{
                    changePasswordWithTuya(email, password)
                }
                MSG_CHANGEPASS_WITH_TUYA_SUCCESS -> {
                    showProgress(false)
                    changePassword(code, email, password)
                }
                MSG_CHANGEPASS_SUCCESS->{
                    showProgress(false)
                    gotoNext()
                }
                MSG_GET_TOKEN_FAILURE ->{
                    getSign(REFRESH_TOKEN)
                }
                MSG_SIGN_REFRESH_SUCCESS->{
                    showProgress(false)
                    refreshToken()
                }
                MSG_REFRESH_TOKEN_SUCCESS ->{
                    getSignToken()
                }
                MSG_VERIFY_CODE_FAILURE , MSG_SIGN_REFRESH_FAILURE, MSG_CHANGEPASS_WITH_TUYA_FAILURE, MSG_REFRESH_TOKEN_FAILURE, MSG_GET_SIGN_FAILURE, MSG_GET_SIGN_TOKEN_FAILURE, MSG_GET_TOKEN_FAILURE -> {
                    showProgress(false)
                    val message = msg.obj as Result
                    showError(message.error)
                }

                else -> {
                    showProgress(false)
                }
            }
        }
        return super.handleMessage(msg)
    }

    private fun getSign(token : Int) {
        checkViewAttached()
        clientId = mvpView?.getClientId()!!
        secret = mvpView?.getSecret()!!
        timeStamp = System.currentTimeMillis()

        dataManager.getSigned(Constant.MODE_SIGN, clientId, null, secret, timeStamp!!)
                .compose<GetSignedResponse>(SchedulerUtils.ioToMain<GetSignedResponse>())
                .subscribe({ response ->
                    when (response.code) {
                        REQUEST_NO_PARAM -> {
                            when(token){
                                TOKEN->{
                                    val msg = MessageUtil.getCallFailMessage(MSG_GET_SIGN_FAILURE, response.code.toString(), response.message)
                                    mHandler?.sendMessage(msg)
                                }
                                REFRESH_TOKEN->{
                                    val msg = MessageUtil.getCallFailMessage(MSG_SIGN_REFRESH_FAILURE, response.code.toString(), response.message)
                                    mHandler?.sendMessage(msg)
                                }
                            }

                        }
                        else -> {
                            Log.d(TAG, "signed: ${response.signed}")
                            signed = response.signed.toUpperCase()
                            when(token){
                                TOKEN ->{
                                    mHandler?.sendEmptyMessage(MSG_GET_SIGN_SUCCESS)
                                }
                                REFRESH_TOKEN->{
                                    mHandler?.sendEmptyMessage(MSG_SIGN_REFRESH_SUCCESS)
                                }
                            }

                        }
                    }
                }) { error ->
                    mvpView?.apply {
                        Log.d(TAG, "$error")
                        showProgress(false)
                        showError(error)
//                    }
                    }

                }
    }

    private fun getSignToken() {
        checkViewAttached()
        timeStamp = System.currentTimeMillis()

        dataManager.getSigned(Constant.MODE_SIGN, clientId, token, secret, timeStamp!!)
                .compose<GetSignedResponse>(SchedulerUtils.ioToMain<GetSignedResponse>())
                .subscribe({ response ->
                    when (response.code) {
                        REQUEST_NO_PARAM -> {
                            val msg = MessageUtil.getCallFailMessage(MSG_GET_SIGN_TOKEN_FAILURE, response.code.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }
                        else -> {
                            Log.d(TAG, "signToken: ${response.signed}")
                            signedToken = response.signed.toUpperCase()
                            mHandler?.sendEmptyMessage(MSG_GET_SIGN_TOKEN_SUCCESS)
                        }
                    }
                }) { error ->
                    mvpView?.apply {
                        Log.d(TAG, "$error")
                        showProgress(false)
                        showError(error)
//                    }
                    }

                }
    }

    private fun getToken() {
        checkViewAttached()
        url = "https://openapi.tuyaus.com/v1.0/token"
        var t = timeStamp.toString()

        val headers = HashMap<String, String>()
        headers["client_id"] = clientId
        headers["sign"] = signed
        headers["t"] = t
        headers["sign_method"] = Constant.HMAC_SHA256

        dataManager.getToken(url, Constant.EASY_SIGN, headers)
                .compose<GetTokenResponse>(SchedulerUtils.ioToMain<GetTokenResponse>())
                .subscribe({ response ->
                    if (response.success) {
                        Log.d(TAG, "Token = ${response.result?.access_token}")
                        Log.d(TAG, "Refresh Token = ${response.result?.refresh_token}")
                        token = response.result?.access_token
                        refresh_token = response.result?.refresh_token
//                        if(!refresh_token.isNullOrBlank()){
//                            token = refresh_token
//                        }

                        mHandler?.sendEmptyMessage(MSG_GET_TOKEN_SUCCESS)
                    } else {
                        Log.d(TAG, "GET_TOKEN_FAILURE: ${response.code}, ${response.msg}")
                        val  msg  = MessageUtil.getCallFailMessage(MSG_GET_TOKEN_FAILURE, response.code.toString(), response.msg!!)
                        mHandler?.sendMessage(msg)
                    }

                }) { error ->
                    mvpView?.apply {
                        Log.d(TAG, "${error.toString()}")
                        showProgress(false)
                        showError(error)
                    }
                }
    }

    private fun changePasswordWithTuya(email: String, password: String) {
        checkViewAttached()
        url = "https://openapi.tuyaus.com/v1.0/apps/${Constant.SCHEMA}/user"
        val t = timeStamp.toString()

        val headers = HashMap<String, String>()
        headers["client_id"] = clientId
        headers["access_token"] = token!!
        headers["sign"] = signedToken
        headers["t"] = t
        headers["sign_method"] = Constant.HMAC_SHA256
        headers["Content-Type"] = Constant.APPLICATION_JSON

        val user = TuyaUser(Constant.PH_COUNTRY_CODE, email, password, "", "2")

        dataManager.registerToTuya(url, headers, user)
                .compose<UserRegistrationResponse>(SchedulerUtils.ioToMain<UserRegistrationResponse>())
                .subscribe({ response ->
                    if (response.success) {
//                        Constant.UID = response.result!!.uid
                        Log.d(TAG, "Registration successful!")
                        mHandler?.sendEmptyMessage(MSG_CHANGEPASS_WITH_TUYA_SUCCESS)
                    } else {
                        Log.d(TAG, "Registration failed: ${response.code.toString()}, ${response.msg}")
                        val msg = MessageUtil.getCallFailMessage(MSG_CHANGEPASS_WITH_TUYA_FAILURE, response.code.toString(), response.msg!!)
                        mHandler?.sendMessage(msg)
                    }

                }) { error ->
                    mvpView?.apply {
                        Log.d(TAG,"error ${error.toString()}")
                        showProgress(false)
                        showError(error)
                    }
                }
    }

    private fun changePassword(code: String, email: String, newPassword: String) {
        dataManager.sendNewPassword(Constant.MODE_FORGOT_PASSWORD, Constant.PROC_FOR_PASSWORD, code, email, newPassword)
                .compose<ChangePasswordResponse>(SchedulerUtils.ioToMain<ChangePasswordResponse>())
                .subscribe({ response ->
                    when (response.code) {
                        CHANGE_PASS_FAILED -> {
                            Log.d(TAG,"CHANGE_PASS_FAILED ${response.message}")
                            val msg = MessageUtil.getCallFailMessage(MSG_CHANGEPASS_FAILURE, CHANGE_PASS_FAILED.toString(), response.message)
                            mHandler?.sendMessage(msg)
                        }
                        else -> {
                            val encyrpted_password = password.md5()
                            password = encyrpted_password
                            mHandler?.sendEmptyMessage(MSG_CHANGEPASS_SUCCESS)
                        }
                    }
                }) { error ->
                    Log.d(TAG,"sendNewPassword ${error.message.toString()}")
                    mvpView?.apply {
                        showProgress(false)
                        showError(error)
                    }
                }
    }

    private fun refreshToken(){
        checkViewAttached()
        Log.d(TAG,"refresh token : $refresh_token")

        url = "https://openapi.tuyaus.com/v1.0/token/$refresh_token"

        var t = timeStamp.toString()

        val headers = HashMap<String, String>()
        headers["client_id"] = clientId
        headers["sign"] = signed
        headers["t"] = t
        headers["sign_method"] = Constant.HMAC_SHA256

        dataManager.refreshToken(url, headers)
                .compose<GetTokenResponse>(SchedulerUtils.ioToMain<GetTokenResponse>())
                .subscribe({ response ->
                    if (response.success) {
                        Log.d(TAG, "Token = ${response.result?.access_token}")
                        Log.d(TAG, "Refresh Token = ${response.result?.refresh_token}")
                        token = response.result?.access_token
                        refresh_token = response.result?.refresh_token
//                        if(!refresh_token.isNullOrBlank()){
//                            token = refresh_token
//                        }

                        mHandler?.sendEmptyMessage(MSG_REFRESH_TOKEN_SUCCESS)
                    } else {
                        Log.d(TAG, "GET_TOKEN_FAILURE: ${response.code}, ${response.msg}")
                        val  msg  = MessageUtil.getCallFailMessage(MSG_REFRESH_TOKEN_FAILURE, response.code.toString(), response.msg!!)
                        mHandler?.sendMessage(msg)
                    }

                }) { error ->
                    mvpView?.apply {
                        Log.d(TAG, "${error.toString()}")
                        showProgress(false)
                        showError(error)
                    }
                }
    }
}