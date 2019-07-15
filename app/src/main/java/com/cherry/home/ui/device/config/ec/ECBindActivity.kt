package com.cherry.home.ui.device.config.ec

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import butterknife.OnClick
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.config.util.BindDeviceUtils
import com.cherry.home.ui.device.model.DeviceBindModel
import com.cherry.home.ui.family.FamilyManager
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import com.company.project.util.ViewUtil
import com.tuya.smart.android.device.utils.WiFiUtil
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.sdk.bean.DeviceBean
import com.wnafee.vector.compat.VectorDrawable
import kotlinx.android.synthetic.main.activity_ec_bind1.*
import kotlinx.android.synthetic.main.dialog_exit.view.*
import java.lang.Boolean.TRUE
import javax.inject.Inject

class ECBindActivity : BaseActivity(), ECBindView {

    override fun layoutId(): Int = R.layout.activity_ec_bind1

    @Inject lateinit var ecBindpresenter: ECBindpresenter

    lateinit var deviceBindModel: DeviceBindModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        ecBindpresenter.attachView(this)

        if(intent.extras != null){
            Constant.MODE = intent.getIntExtra(ECPresenter.CONFIG_MODE, ECBindpresenter.EC_MODE)
            Constant.CONFIG_PASSWORD = intent.getStringExtra(ECPresenter.CONFIG_PASSWORD)
            Constant.CONFIG_SSID = intent.getStringExtra(ECPresenter.CONFIG_SSID)

        }

        initToolbar()
        setToolbarTitle(R.string.ty_ez_connecting_device_title)

        initView()
        onClick()
        initPresenter()

        circleView.setValueInterpolator(LinearInterpolator())
        deviceBindModel = DeviceBindModel(this, ecBindpresenter.getPresenterHandler())

    }

    override fun onResume() {
        super.onResume()
        registerWifiReceiver()
        checkSSIDAndGoNext()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterWifiReceiver()
        ecBindpresenter.onDestroy()
        ecBindpresenter.detachView()
    }

    override fun snackBarLayoutId(): ViewGroup = ec_bind_id


    private fun initView()
    {
        val drawable = VectorDrawable.getDrawable(TuyaSdk.getApplication(), R.drawable.add_device_fail_icon)
        iv_add_device_fail.setImageDrawable(drawable)

        val color = ViewUtil.getColor(this, R.color.navbar_font_color)
        circleView.setBarColor(color)
        circleView.setSpinBarColor(color)
        circleView.setTextColor(color)
        circleView.setUnitColor(color)
        circleView.rimColor = Color.argb(51, Color.red(color), Color.green(color), Color.blue(color))

    }

    override fun showProgress(show: Boolean) {
        when(show){
            TRUE -> {
                ProgressUtil.showLoading(this, R.string.loading)
            }
            else ->{
                ProgressUtil.hideLoading()
            }
        }
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSuccessPage() {
        setAddDeviceTipGone()
        setViewVisible(tv_add_device_success)
        setViewVisible(tv_finish_button)
//        setViewVisible(tv_share_button)
        setViewGone(ec_connecting)
    }

    override fun showFailurePage() {
        setAddDeviceTipGone()
        setToolbarTitle(R.string.ty_ap_error_title)
        setViewGone(ec_connecting)
        setViewVisible(ll_failure_view)
        setViewVisible(tv_retry_button)
        tv_ec_find_search_help.setText(R.string.ty_ap_error_description)
    }

    override fun showConnectPage() {
        setToolbarTitle(R.string.ty_ez_connecting_device_title)
        setViewVisible(ec_connecting)
        setViewGone(ll_failure_view)
        setViewGone(tv_retry_button)
//        setViewGone(tv_add_device_contact_tip)
    }

    override fun setConnectProgress(progress: Float, animationDuration: Int) {
        circleView.setValueAnimated(progress, animationDuration.toLong());

    }

    override fun showNetWorkFailurePage() {
        showFailurePage()
//        setViewGone(tv_add_device_contact_tip)
        tv_ec_find_search_help.setText(R.string.network_time_out)

    }

    override fun showBindDeviceSuccessTip() {
        ViewUtil.setTextViewDrawableLeft(this, tv_bind_success, R.drawable.ty_add_device_ok_tip)
    }

    override fun showDeviceFindTip(gwId: String) {
        ViewUtil.setTextViewDrawableLeft(this, tv_dev_find, R.drawable.ty_add_device_ok_tip)
    }

    override fun showConfigSuccessTip() {
        ViewUtil.setTextViewDrawableLeft(this, tv_device_init, R.drawable.ty_add_device_ok_tip)
    }

    override fun showBindDeviceSuccessFinalTip() {
        showSuccessPage()
        setViewVisible(tv_device_init_tip)
    }

    override fun setAddDeviceName(name: String) {
        tv_add_device_success.setText(name)
    }

    override fun showMainPage() {
        setViewVisible(tv_dev_find)
        setViewVisible(tv_bind_success)
        setViewVisible(tv_device_init)
    }

    override fun hideMainPage() {
        setViewGone(ec_connecting)
        setViewGone(tv_finish_button)
//        setViewGone(tv_share_button)
        setViewGone(tv_retry_button)
//        setViewGone(tv_add_device_contact_tip)
        setViewGone(tv_add_device_success)
        setViewGone(tv_device_init_tip)
        setViewGone(ll_failure_view)
        setViewGone(tv_dev_find)
        setViewGone(tv_bind_success)
        setViewGone(tv_device_init)
    }

    override fun showSubPage() {
        setViewVisible(switch_wifi_layout)
    }

    override fun hideSubPage() {
        setViewGone(switch_wifi_layout)
    }


    override fun getHomeID(): Long {
        return FamilyManager.getInstance(this).getCurrentHomeId()
    }

    override fun setAP(ssid : String, password: String, token : String) {
        deviceBindModel.setAP(ssid, password, token)
    }

    override fun setEC(ssid : String, password: String, token : String) {
        deviceBindModel.setEC(ssid, password, token)
    }

    override fun start() {
        deviceBindModel.start()
    }

    override fun gotoAddDeviceTipActivity() {
        ActivityUtils.startActivity(this, Intent(this, AddDeviceTipActivity::class.java), ActivityUtils.ANIMATE_FORWARD, true)
    }

    override fun gotogoToEZActivity() {
        val intent = Intent(this, ECActivity::class.java)
        ActivityUtils.startActivity(this as Activity, intent, ActivityUtils.ANIMATE_FORWARD, true)
    }

    override fun showToast(deviceBean: DeviceBean) {
    }

    override fun configFailure() {
        deviceBindModel.configFailure()
    }

    override fun cancel() {
        deviceBindModel.cancel()
    }

    override fun destroy() {
        deviceBindModel.onDestroy()
    }

    override fun getSSID(): String {
        return WiFiUtil.getCurrentSSID(this)
    }

    override fun removeNetwork(currentSSID: String) {
        WiFiUtil.removeNetwork(this, currentSSID)
    }

    override fun onBackPressed() {
        showEditName()

    }

    private fun showEditName(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.dialog_ok_exit.setOnClickListener {
            mAlertDialog.dismiss()
            finish()
        }

        mDialogView.dialog_cancel_exit.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


    private fun initPresenter(){
        ecBindpresenter.showConfigDevicePage()
        ecBindpresenter.getTokenForConfigDevice()
    }

    private fun setAddDeviceTipGone() {
        setViewGone(tv_bind_success)
        setViewGone(tv_dev_find)
        setViewGone(tv_device_init)
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                val networkInfo = intent.getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo
                if (networkInfo.isAvailable && networkInfo.isConnected) {
                    checkSSIDAndGoNext()
                }
            }
        }
    }

    private fun registerWifiReceiver() {
        try {
            registerReceiver(mBroadcastReceiver, IntentFilter(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun unRegisterWifiReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDisplayHomeAsUpEnabled() {
        setDisplayHomeAsUpEnabled(R.drawable.tysmart_back_white, null)
    }

    private fun setDisplayHomeAsUpEnabled(listener: View.OnClickListener) {
        setDisplayHomeAsUpEnabled(R.drawable.tysmart_back_white, listener)
    }

    private fun checkSSIDAndGoNext() {
        if (isPause()) return
        if (BindDeviceUtils.isAPMode()) {
            unRegisterWifiReceiver()
            hideSubPage()
            showMainPage()
            ecBindpresenter.startSearch()
        } else {
            Log.d("ECBind","checkSSIDAndGoNext(): not APMode()")
        }
    }

    @OnClick(R.id.tv_bottom_button)
    fun onClickSettings() {
        var wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
        if (null != wifiSettingsIntent.resolveActivity(packageManager)) {
            startActivity(wifiSettingsIntent)
        } else {
            wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            if (null != wifiSettingsIntent.resolveActivity(packageManager)) {
                startActivity(wifiSettingsIntent)
            }
        }
    }

    private fun onClickRetry() {
        circleView.setValue(0f)
        ecBindpresenter.reStartEZConfig()
    }

    private fun onClickConnect() {
        ecBindpresenter.gotoShareActivity()
    }

    private fun onClickFinish() {
        Constant.finishActivity()
        ActivityUtils.gotoMainActivity(this)
    }

    private fun onClickFins() {
        ecBindpresenter.goForHelp()
    }

    private fun onClick(){
        tv_retry_button.setOnClickListener {
            onClickRetry()
        }

//        tv_share_button.setOnClickListener {
//            onClickConnect()
//        }

        tv_finish_button.setOnClickListener {
            onClickFinish()
        }

        tv_ec_find_search_help.setOnClickListener {
            onClickFins()
        }
    }



}