package com.cherry.home.ui.device.config.ec

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.DialogUtil
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.tuya.smart.android.common.utils.NetworkUtil
import com.tuya.smart.android.device.utils.WiFiUtil
import com.tuya.smart.sdk.TuyaSdk
import com.wnafee.vector.compat.VectorDrawable
import kotlinx.android.synthetic.main.activity_ec_mode.*
import javax.inject.Inject

class ECActivity : BaseActivity(), ECView, TextWatcher {


    private val TAG : String = "ECActivity"

    @Inject
    lateinit var ecPresenter: ECPresenter

    private var passwordOn: Boolean = false

    private var mMode: Int = 0

    private var mWifiOnColor: Int = 0

    override fun layoutId(): Int = R.layout.activity_ec_mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        ecPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.ty_ez_wifi_title)
        initView()
        initListener()

        mMode = intent.getIntExtra(ECPresenter.CONFIG_MODE, ecPresenter.EC_MODE)

        ecPresenter.showLocation()
        et_password.addTextChangedListener(this)
    }

    override fun snackBarLayoutId(): ViewGroup = ec_rl

    override fun onResume() {
        super.onResume()
        registerWifiReceiver()
        ecPresenter.checkWifiNetworkStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterWifiReceiver()
        ecPresenter.detachView()
    }

    override fun onPanelKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun showSnackbar(msg: Int) {
        showSnack(ec_rl, msg, Snackbar.LENGTH_LONG, null)
    }

    override fun showDialog(ssid: String, passWord: String) {
        DialogUtil.customDialog(this, null, getString(R.string.ez_notSupport_5G_tip), getString(R.string.ez_notSupport_5G_change), getString(R.string.ez_notSupport_5G_continue), null) { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> userOtherWifi()
                DialogInterface.BUTTON_NEGATIVE -> gotoBindDeviceActivity(ssid, passWord)
            }
        }.show()
    }

    override fun setWifiSSID(ssId: String) {
        Log.d(TAG, "ssid: $ssId")
        tv_network.visible()
        iv_wifi.setColorFilter(mWifiOnColor)
        tv_wifi_status.text = getString(R.string.ty_ez_current_wifi_android)
        tv_network.text = ssId
    }

    override fun setWifiPass(pass: String) {
        et_password.setText(pass)
    }

    override fun getWifiPass(): String = et_password.text.toString()

    override fun getWifiSsId(): String = tv_network.text.toString()

    override fun showNoWifi() {
        tv_network.gone()
        hide5gTip()
        setWifiPass("")
        iv_wifi.setColorFilter(Color.GRAY)
        tv_wifi_status.text = getString(R.string.ty_ez_current_wifi_android)
    }

    override fun show5gTip() {
        setViewVisible(network_tip)
    }

    override fun hide5gTip() {
        setViewGone(network_tip)
    }

    override fun isWifiDisabled(): Boolean {
        val mWifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return mWifiManager != null && !mWifiManager.isWifiEnabled
    }

    override fun checkSystemGPSLocation(): Boolean {
        val isOpen: Boolean
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        if (!isOpen) {
            AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.ty_simple_confirm_title)
                    .setMessage(R.string.ty_notify_location_setup)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.setup) { dialogInterface, i ->
                        gotoLocationSetting()
                        dialogInterface.dismiss()
                    }
                    .show()
        }
        return isOpen
    }

    override fun checkSinglePermission(permission: String, resultCode: Int): Boolean {
        val hasPermission: Boolean
        if (Build.VERSION.SDK_INT < 23) {
            return true
        } else {
            hasPermission = hasPermission(permission)
        }
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), resultCode)
            return false
        }
        return true
    }

    override fun getCurrentSSID(): String {
        return WiFiUtil.getCurrentSSID(this)
    }

    override fun isNetworkAvailable(): Boolean{
        return NetworkUtil.isNetworkAvailable(this)
    }

    override fun is5GHz(ssid: String): Boolean {
        val wifiManger = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManger.connectionInfo
        return if (wifiInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val freq = wifiInfo.frequency
            freq in 4901..5899
        } else
            ssid.toUpperCase().endsWith("5G")
    }

    override fun gotoBindDeviceActivity(ssid: String, passWord: String) {
        val intent = Intent(this, ECBindActivity::class.java)
        intent.putExtra(ECPresenter.CONFIG_PASSWORD, passWord)
        intent.putExtra(ECPresenter.CONFIG_SSID, ssid)
        intent.putExtra(ECPresenter.CONFIG_MODE, mMode)
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD, true)
    }

    override fun afterTextChanged(p0: Editable?) {
        if (et_password.text.isNullOrBlank()){
            disableLogin()
        }else{
            enableLogin()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    private fun enableLogin(){
        if (!tv_bottom_button.isEnabled){
            tv_bottom_button.isEnabled = true
            tv_bottom_button.setBackgroundResource(R.drawable.gradient_red_square)
        }
    }

    private fun disableLogin(){
        if (tv_bottom_button.isEnabled){
            tv_bottom_button.isEnabled = false
            tv_bottom_button.setBackgroundResource(R.drawable.gray_button_bg)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        var targetSdkVersion = 0
        try {
            val info = packageManager.getPackageInfo(
                    packageName, 0)
            targetSdkVersion = info.applicationInfo.targetSdkVersion
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        var result = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED
            }
        }

        return result
    }

    private fun registerWifiReceiver() {
        try {
            registerReceiver(mBroadcastReceiver, IntentFilter(
                    WifiManager.NETWORK_STATE_CHANGED_ACTION))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                ecPresenter.checkWifiNetworkStatus()
            }
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

    private fun initListener() {
        tv_other_wifi.setOnClickListener {
            userOtherWifi()
        }

        ec_password_switch.setOnClickListener {
            passwordOn = !passwordOn
            showPassword(passwordOn)
        }

        tv_bottom_button.setOnClickListener {
            ecPresenter.goNextStep()
        }
    }

    private fun showPassword(show: Boolean) {
        when (show) {
            true -> {
                ec_password_switch.setImageResource(R.drawable.ty_password_on)
                et_password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            false -> {
                ec_password_switch.setImageResource(R.drawable.ty_password_off)
                et_password.inputType = InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun userOtherWifi() {
        var wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
        if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
            startActivity(wifiSettingsIntent)
        } else {
            wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            if (null != wifiSettingsIntent.resolveActivity(getPackageManager())) {
                startActivity(wifiSettingsIntent)
            }
        }
    }

    private fun setWifiVectorDrawable(view: ImageView) {
        val drawable = VectorDrawable.getDrawable(TuyaSdk.getApplication(), R.drawable.wifi_status)
        iv_wifi.background = resources.getDrawable(R.drawable.bg_bt_circle)
        drawable.alpha = 128
        view.setImageDrawable(drawable)
    }

    private fun initView() {
        tv_network.visible()

        val a = obtainStyledAttributes(intArrayOf(R.attr.navbar_font_color))
        mWifiOnColor = a.getColor(0, resources.getColor(R.color.black))
        a.recycle()

        setWifiVectorDrawable(iv_wifi)
        iv_wifi.setColorFilter(mWifiOnColor)
    }

    private fun gotoLocationSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, ECPresenter.PRIVATE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ECPresenter.PRIVATE_CODE -> {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
                if (isOpen) {
                    checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, ECPresenter.CODE_FOR_LOCATION_PERMISSION)
                }
            }
        }
    }


}