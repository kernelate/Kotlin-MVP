package com.cherry.home.ui.device.config.ap

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.config.ec.ECActivity
import com.cherry.home.ui.device.config.ec.ECBindActivity
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_add_device_tip.*

class AddDeviceAPTipActivity : BaseActivity() {

    private val TAG: String = "AddDeviceAPTipActivity"
    private val FROM_EZ_FAILURE : String = "FROM_EZ_FAILURE"

    private val AP_MODE = 0

    lateinit var animationDrawable: AnimationDrawable

    override fun layoutId(): Int = R.layout.activity_add_device_tip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        initToolbar()
        initView()
        initListener()
        setToolbarTitle(R.string.choose_wifi)
    }

    override fun onBackPressed() {
        val intent = Intent(this, AddDeviceTipActivity::class.java)
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_BACK, true)
    }

    override fun onPause() {
        super.onPause()
        if (animationDrawable != null && animationDrawable.isRunning) {
            animationDrawable.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (animationDrawable != null && animationDrawable.isRunning) {
            animationDrawable.start()
        }

        status_light_option.text = getString(R.string.ty_add_device_ap_btn_info)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (animationDrawable != null && animationDrawable.isRunning) {
            animationDrawable.stop()
        }
    }

    override fun snackBarLayoutId(): ViewGroup = add_tip_id

    private fun initView() {
        status_light_tip.text = getString(R.string.ty_add_device_ap_info)

        animationDrawable = AnimationDrawable()
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_lighting), 1500)
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_light), 1500)
        animationDrawable.isOneShot = false
        status_light_imageview.setImageDrawable(animationDrawable)
        animationDrawable.start()


        if (intent.getBooleanExtra(FROM_EZ_FAILURE, false)) {
            status_light_tip.setText(R.string.ty_add_device_ez_failure_ap_tip)
        }
    }

    private fun initListener() {
        status_light_option.setOnClickListener {
            gotoNextActivity()
        }

        status_light_help.setOnClickListener {
            ActivityUtils.gotoAddDeviceHelpActivity(this, getString(R.string.ty_ez_help))
        }
    }

    private fun gotoNextActivity() {
        if (intent.getBooleanExtra(FROM_EZ_FAILURE, false)) {
            gotoApTipActivity()
        } else {
            gotoWifiInputActivity()
        }
    }

    private fun gotoWifiInputActivity() {
        val intent = Intent(this@AddDeviceAPTipActivity, ECActivity::class.java)
        intent.putExtra(Constant.CONFIG_MODE, AP_MODE)
        ActivityUtils.startActivity(this@AddDeviceAPTipActivity, intent, ActivityUtils.ANIMATE_FORWARD, true)
    }

    private fun gotoApTipActivity() {
        val getIntent = intent
        val intent = Intent(this, ECBindActivity::class.java)
        intent.putExtra(Constant.CONFIG_PASSWORD, getIntent.getStringExtra(Constant.CONFIG_PASSWORD))
        intent.putExtra(Constant.CONFIG_SSID, getIntent.getStringExtra(Constant.CONFIG_SSID))
        intent.putExtra(Constant.CONFIG_MODE, AP_MODE)
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, true)
    }
}