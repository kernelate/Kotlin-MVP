package com.cherry.home.ui.device.config

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.config.ap.AddDeviceAPTipActivity
import com.cherry.home.ui.device.config.ec.ECActivity
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_add_device_tip.*

class AddDeviceTipActivity : BaseActivity() {

    internal val EC_MODE = 1

    lateinit var animationDrawable: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        initToolbar()
        setToolbarTitle(R.string.choose_wifi)
        initTipImageView()
        initListener()

    }

    override fun layoutId(): Int = R.layout.activity_add_device_tip

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_ap_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_ap_mode_onclick -> {
            val intent = Intent(this@AddDeviceTipActivity, AddDeviceAPTipActivity::class.java)
            ActivityUtils.startActivity(this@AddDeviceTipActivity, intent, ActivityUtils.ANIMATE_FORWARD, true)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun initTipImageView()
    {
        animationDrawable = AnimationDrawable()
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_lighting), 250);
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.drawable.ty_adddevice_light), 250);
        animationDrawable.setOneShot(false);
        status_light_imageview.setImageDrawable(animationDrawable)
        animationDrawable.start()
    }

    override fun onBackPressed() {
        ActivityUtils.back(this, ActivityUtils.ANIMATE_SLIDE_BOTTOM_FROM_TOP)
    }

    override fun onPause() {
        super.onPause()
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.start()
        }

        status_light_option.text = getString(R.string.ty_add_device_ec_btn_info)
    }

    override fun onStop() {
        super.onStop()
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop()
        }
    }

    override fun snackBarLayoutId(): ViewGroup = add_tip_id

    private fun initListener(){
        status_light_option.setOnClickListener {
            val intent = Intent(this@AddDeviceTipActivity, ECActivity::class.java)
            intent.putExtra(Constant.CONFIG_MODE, EC_MODE)
            ActivityUtils.startActivity(this@AddDeviceTipActivity, intent, ActivityUtils.ANIMATE_FORWARD, true)
        }

        status_light_help.setOnClickListener {
            ActivityUtils.gotoAddDeviceHelpActivity(this, getString(R.string.ty_ez_help))
        }
    }
}