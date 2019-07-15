package com.cherry.home.ui.signup.forgotpass.validate

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.login.LoginActivity
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_change_pass_success.*

class SuccessActivity : BaseActivity() {

    override fun layoutId(): Int = R.layout.activity_change_pass_success

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        btn_login.setOnClickListener {
            Constant.finishActivity()
            val i = Intent(this, LoginActivity::class.java)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SCALE_OUT, true)
        }
    }

    override fun needLogin(): Boolean = false

    override fun snackBarLayoutId(): ViewGroup = success_pw_id
}