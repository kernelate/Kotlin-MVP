package com.cherry.home.ui.signup.forgotpass

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.signup.forgotpass.validate.CodeValidationActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import com.tuya.smart.android.common.utils.ValidatorUtil
import kotlinx.android.synthetic.main.activity_family_empty.*
import kotlinx.android.synthetic.main.activity_send_code.*
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import javax.inject.Inject


class ForgotPasswordActivity : BaseActivity(), ForgotPasswordView, TextWatcher {


    private val TAG : String = "ForgotPasswordActivity"

    @Inject
    lateinit var forgotPasswordPresenter: ForgotPasswordPresenter

    override fun layoutId(): Int = R.layout.activity_send_code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        forgotPasswordPresenter.attachView(this)

        send_email.addTextChangedListener(this)

        btn_send.setOnClickListener {
            if(btn_send.isEnabled){
                enableSendButton(false)
                showProgress(true)
                val email = send_email.text.toString()
                forgotPasswordPresenter.sendEmail(email)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        forgotPasswordPresenter.detachView()
    }

    override fun needLogin(): Boolean = false

    override fun afterTextChanged(p0: Editable?) {
        val email = send_email.text.toString()

        if(send_email.text.isNullOrBlank()){
            textInputLayoutSendEmail.isErrorEnabled = true
            textInputLayoutSendEmail.error = getString(R.string.email_empty)
            enableSendButton(false)
        } else {
            if (ValidatorUtil.isEmail(email)) {
                textInputLayoutSendEmail.isErrorEnabled = false
                enableSendButton(true)
            } else {
                textInputLayoutSendEmail.isErrorEnabled = true
                textInputLayoutSendEmail.error = getString(R.string.email_invalid)
                enableSendButton(false)
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            ProgressUtil.showLoading(this, R.string.progress_sending_code)
        } else {
            ProgressUtil.hideLoading()
        }
    }

    override fun showError(error_msg: String) {
        showSnack(send_verification_id, error_msg, Snackbar.LENGTH_LONG, null)
    }

    override fun showError(error: Throwable) {
        showSnack(send_verification_id, error.toString(), Snackbar.LENGTH_LONG, null)
    }

    override fun gotoNext() {
        val email = send_email.text.toString()

        val i = Intent(this, CodeValidationActivity::class.java)
        i.putExtra("intent_email", email)
        ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, false)
    }

    override fun snackBarLayoutId(): ViewGroup = send_verification_id

    private fun enableSendButton(enabled : Boolean){
        when(enabled){
            TRUE ->{
                if(!btn_send.isEnabled){
                    btn_send.isEnabled = true
                    btn_send.setBackgroundResource(R.drawable.gradient_red)
                }
            }
            FALSE ->{
                if(btn_send.isEnabled){
                    btn_send.isEnabled = false
                    btn_send.setBackgroundResource(R.drawable.gray_button_bg)
                }
            }
        }
    }
}