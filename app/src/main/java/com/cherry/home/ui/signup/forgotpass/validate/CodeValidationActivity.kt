package com.cherry.home.ui.signup.forgotpass.validate

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cherry.home.BuildConfig
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import kotlinx.android.synthetic.main.activity_new_password.*
import kotlinx.android.synthetic.main.confirm_dialog.*
import javax.inject.Inject

class CodeValidationActivity : BaseActivity(), CodeValidationView, TextWatcher {

    private val TAG : String = "CodeValidationActivity"

    @Inject
    lateinit var codeValidationPresenter: CodeValidationPresenter

    lateinit var email: String

    override fun layoutId(): Int = R.layout.activity_new_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        codeValidationPresenter.attachView(this)

        email = intent.getStringExtra(codeValidationPresenter.INTENT_EMAIL)
        if (!email.isEmpty()) {
            showDialog()
        }
        initOnClick()

        verification_code.addTextChangedListener(this)
        new_password.addTextChangedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        codeValidationPresenter.onDestroy()
        codeValidationPresenter.detachView()
    }

    override fun needLogin(): Boolean = false

    override fun afterTextChanged(p0: Editable?) {
        val password = new_password.text.toString()
        if (verification_code.text.isNullOrBlank()) {
            textInputLayoutVerifyCode.isErrorEnabled = true
            textInputLayoutVerifyCode.error = getString(R.string.code_empty)
            disableChangePass()
        } else {
            enableChangePass()
            textInputLayoutVerifyCode.isErrorEnabled = false
            if (new_password.text.isNullOrBlank()) {
                disableChangePass()
            }
            else {
                if(password.length >= 8){
                    textInputLayoutNewPassword.isErrorEnabled = false
//                    textInputLayoutNewPassword.error = getString(R.string.code_empty)
                    enableChangePass()
                } else {
                    textInputLayoutVerifyCode.isErrorEnabled = true
                    textInputLayoutNewPassword.error = getString(R.string.error_password)
                    disableChangePass()
                }
            }
        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            ProgressUtil.showLoading(this, R.string.progress_verifying_code)
        } else {
            ProgressUtil.hideLoading()
        }
    }

    override fun showError(error_msg: String) {
        showSnack(code_validation_id, error_msg, Snackbar.LENGTH_LONG, null)
    }

    override fun showError(error: Throwable) {
        showSnack(code_validation_id, error.toString(), Snackbar.LENGTH_LONG, null)
    }

    override fun getClientId(): String = BuildConfig.client_id

    override fun getSecret(): String = BuildConfig.secret

    override fun getURL(): String = BuildConfig.url

    override fun gotoNext() {
        val i = Intent(this, SuccessActivity::class.java)
        ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SLIDE_TOP_FROM_BOTTOM, false)
    }

    override fun snackBarLayoutId(): ViewGroup = code_validation_id

    private fun enableChangePass() {
        if (!btn_chgpass.isEnabled) {
            btn_chgpass.isEnabled = true
            btn_chgpass.setBackgroundResource(R.drawable.gradient_red)
        }
    }

    private fun disableChangePass() {
        if (btn_chgpass.isEnabled) {
            btn_chgpass.isEnabled = false
            btn_chgpass.setBackgroundResource(R.drawable.gray_button_bg)
        }
    }

    private fun initOnClick() {
        btn_chgpass.setOnClickListener {
            if (btn_chgpass.isEnabled) {
                showProgress(true)
                disableChangePass()
                val code = verification_code.text.toString()
                val password = new_password.text.toString()
                codeValidationPresenter.verifyCode(code, email, password)
                clearPassword()
            }
        }
    }

    private fun clearPassword() {
        new_password.text?.clear()
    }

    private fun showDialog() {
        // Initialize a new instance of
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()
        mAlertDialog.verify_ok.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}