package com.cherry.home.ui.login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.signup.SignupActivity
import com.cherry.home.ui.signup.forgotpass.ForgotPasswordActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.android.common.utils.ValidatorUtil
import com.tuya.smart.android.mvp.bean.Result
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.confirm_dialog.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginMvpView, TextWatcher{


    private val TAG : String = "LoginActivity"
    private var passwordOn : Boolean = false

    @Inject
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        loginPresenter.attachView(this)

        email.addTextChangedListener(this)
        password.addTextChangedListener(this)
        clickListeners()

    }

    override fun layoutId() = R.layout.activity_login

    override fun showProgress(show: Boolean) {
        if(show){
            ProgressUtil.showLoading(this, R.string.logging_in)
        } else {
            ProgressUtil.hideLoading()
        }
    }

    override fun showError(error: Throwable) {
        showError(error.toString())
        enableLogin()
    }

    override fun showError(error_msg: String) {
        showSnack(snackBarLayoutId(), error_msg, Snackbar.LENGTH_LONG, null)
        enableLogin()
    }


    override fun openMainActivity() {
        ActivityUtils.gotoMainActivity(this)
    }

    override fun gotoSignupActivity() {
        SignupActivity.gotoSignupActivityForResult(this, loginPresenter.REQUEST_LOGIN)
    }


    override fun snackBarLayoutId(): ViewGroup = nestedScrollView2

    override fun afterTextChanged(editable: Editable?) {
        val userName = email.text.toString()

        if(email.text.isNullOrBlank()){
            disableLogin()
            textInputLayoutEmail.error = getString(R.string.email_empty)
        } else {
            if(password.text.isNullOrBlank()){
                disableLogin()
            }

            if(ValidatorUtil.isEmail(userName) && !password.text.isNullOrBlank()){
                textInputLayoutEmail.error = null
                enableLogin()
            } else {
                disableLogin()
                textInputLayoutEmail.error = getString(R.string.email_invalid)
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun loginResult(what: Int, result: Result?) {
        when (what) {
            loginPresenter.MSG_LOGIN_SUCCESS -> {
                showProgress(false)
            }
            loginPresenter.MSG_LOGIN_FAILURE, loginPresenter.MSG_REGISTER_FAILURE, loginPresenter.MSG_ACTIVATE_FAILURE -> {
                clearPassword()
                showError(result!!.error)
                enableLogin()
            }
        }
    }


    override fun needLogin(): Boolean {
        return false
    }

    override fun showDialog() {
        // Initialize a new instance of
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        mAlertDialog.verify_ok.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun clearPassword() {
        password.text!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter.onDestroy()
        loginPresenter.detachView()

    }



    fun enableLogin(){
        if(!btn_login2.isEnabled){
            btn_login2.isEnabled = true
            btn_login2.setBackgroundResource(R.drawable.gradient_red_square)
        }
    }

    private fun disableLogin(){
        if(btn_login2.isEnabled){
            btn_login2.isEnabled = false
            btn_login2.setBackgroundResource(R.drawable.gray_button_bg)
        }
    }

    private fun clickListeners(){
        btn_login2.setOnClickListener {
            if(btn_login2.isEnabled){
                disableLogin()
                showProgress(true)
                loginPresenter.login(email.text.toString(), password.text.toString())
            }
        }

        txt_signup2.setOnClickListener {
            gotoSignupActivity()
        }

        txt_forgotpw2.setOnClickListener {
            val i = Intent(this, ForgotPasswordActivity::class.java)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
        }

    }

}