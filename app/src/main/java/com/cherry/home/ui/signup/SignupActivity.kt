package com.cherry.home.ui.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import com.tuya.smart.android.common.utils.ValidatorUtil
import com.tuya.smart.android.mvp.bean.Result
import kotlinx.android.synthetic.main.activity_signup.*
import javax.inject.Inject

class SignupActivity : BaseActivity(), SignupView, TextWatcher {

    private val TAG : String = "SignupActivity"

    @Inject
    lateinit var signupPresenter: SignupPresenter

    private var passwordOn: Boolean? = null

    companion object {
        fun gotoSignupActivityForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, SignupActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, 0, false)
        }
    }

    override fun layoutId() = R.layout.activity_signup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        signupPresenter.attachView(this)

        passwordOn = false
        register_email2.addTextChangedListener(this)
        register_password2.addTextChangedListener(this)
        clickListeners()

    }

    override fun showProgress(show: Boolean) {
        if (show) {
            ProgressUtil.showLoading(this, R.string.register_in)
        } else {
            ProgressUtil.hideLoading()
        }
    }

    override fun showError(error: Throwable) {
        enableRegisterBtn(false)
        showSnack(snackBarLayoutId(), error.toString(), Snackbar.LENGTH_LONG, null)
    }

    override fun showError(result: Result) {
        enableRegisterBtn(false)
        showSnack(snackBarLayoutId(), result.error, Snackbar.LENGTH_LONG, null)
    }

    override fun gotoLogin() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        ActivityUtils.back(this)
    }

    override fun needLogin(): Boolean = false

    override fun getAccount(): String = register_email2.text.toString()

    override fun enableRegisterBtn(enable: Boolean) {
        when (enable) {
            true -> {
                if (!btn_register2.isEnabled) {
                    btn_register2.isEnabled = true
                    btn_register2.setBackgroundResource(R.drawable.gradient_red)
                }
            }
            false -> {
                if (btn_register2.isEnabled) {
                    btn_register2.isEnabled = false
                    btn_register2.setBackgroundResource(R.drawable.gray_button_bg)
                }
            }
        }
    }

    override fun afterTextChanged(editable: Editable?) {
        val username = register_email2.text.toString()

        if (register_password2.text.isNullOrBlank() || register_email2.text.isNullOrBlank()) {
            enableRegisterBtn(false)
        }

        if (!ValidatorUtil.isEmail(username)) {
            enableRegisterBtn(false)
        } else {
            if (!register_password2.text.isNullOrBlank()) {
                enableRegisterBtn(true)
            }

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun clearPassword() {
        register_password2.text!!.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(signupPresenter != null){
            signupPresenter.onDestroy()
            signupPresenter.detachView()
        }
    }

    override fun snackBarLayoutId(): ViewGroup = nestedScrollView

    private fun clickListeners() {

        txt_login2.setOnClickListener {
            onBackPressed()
        }

        btn_register2.setOnClickListener {
            if (btn_register2.isEnabled) {
                enableRegisterBtn(false)
                val user = register_email2.text.toString()
                val password = register_password2.text.toString()
                if(!user.isNullOrBlank() && !password.isNullOrBlank()){
                    showProgress(true)
                    signupPresenter.registerUser(register_email2.text.toString(), register_password2.text.toString())
                }
            }
        }
    }
}