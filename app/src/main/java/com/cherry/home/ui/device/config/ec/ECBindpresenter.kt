package com.cherry.home.ui.device.config.ec

import android.os.Message
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.device.config.util.BindDeviceUtils
import com.cherry.home.ui.device.model.DeviceBindModel
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.common.utils.SafeHandler
import com.tuya.smart.android.mvp.bean.Result
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.interior.device.bean.GwDevResp
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.bean.DeviceBean
import javax.inject.Inject

@ConfigPersistent
class ECBindpresenter @Inject constructor() : BasePresenter<ECBindView>() {

    private val TAG : String = "ECBindPresenter"



    private var mBindDeviceSuccess: Boolean = false
    private var mStop: Boolean = false

    private var mTime: Int = 0

    private val MESSAGE_CONFIG_WIFI_OUT_OF_TIME = 0x16
    private val MESSAGE_SHOW_SUCCESS_PAGE = 1001

    companion object {
        internal val EC_MODE = 1
        internal val AP_MODE = 0
    }

    init {
        getHandler()
    }

    fun showConfigDevicePage() {
        Log.d(TAG, "showConfigDevicePage")
        mvpView?.apply {
            if (Constant.MODE == EC_MODE) {
                hideSubPage()
            } else {
                hideMainPage()
                showSubPage()
            }
        }

    }

    fun getTokenForConfigDevice(){
        Log.d(TAG, "getTokenForConfigDevice")
        mvpView?.apply {
            showProgress(true)
            var homeId = getHomeID()
            Log.d(TAG, "homeId: $homeId")
            TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId, object : ITuyaActivatorGetToken {
                override fun onSuccess(token: String) {
                    showProgress(false)
                    Log.d(TAG,"showProgress ")
                    initConfigDevice(token)
                }

                override fun onFailure(s: String, s1: String) {
                    showProgress(false)
                    if (Constant.MODE == EC_MODE) {
                        Log.d(TAG,"constant : ${Constant.MODE} | EC_MODE : $EC_MODE")
                        showNetWorkFailurePage()
                    }
                }
            })
        }
    }

    private fun initConfigDevice(token : String ){
        Log.d(TAG,"initConfigDevice ")
        mvpView?.apply {
            if (Constant.MODE == EC_MODE) {
                Log.d(TAG,"initConfigDevice 11")
                setEC(Constant.CONFIG_SSID, Constant.CONFIG_PASSWORD, token)
                startSearch()
            } else if (Constant.MODE == AP_MODE) {
                Log.d(TAG,"initConfigDevice 22")
                setAP(Constant.CONFIG_SSID, Constant.CONFIG_PASSWORD, token)
            }
        }

    }

    open fun startSearch() {
        Log.d(TAG,"startSearch")
        mvpView?.apply {
            start()
            showConnectPage()
            mBindDeviceSuccess = false
            startLoop()
        }

    }

    private fun startLoop(){
        Log.d(TAG,"startLoop")
        mTime = 0
        mStop = false
        mHandler?.sendEmptyMessage(MESSAGE_CONFIG_WIFI_OUT_OF_TIME)
    }

    fun getPresenterHandler() : SafeHandler{
        return mHandler!!
    }

    fun reStartEZConfig() {
        Log.d(TAG,"reStartEZConfig")
        mvpView?.apply {
            if (Constant.MODE == AP_MODE) {
                Log.d(TAG,"reStartEZConfig 11  ${Constant.MODE}  |  AP  $AP_MODE" )
                gotoAddDeviceTipActivity()
            } else {
                Log.d(TAG,"reStartEZConfig 22")
                goToEZActivity()
            }
        }
    }

    private fun goToEZActivity() {
        Log.d(TAG,"goToEZActivity")
        mvpView?.gotogoToEZActivity()
    }

    private fun bindDeviceSuccess(name: String) {
        Log.d(TAG,"bindDeviceSuccess ")
        mvpView?.apply {
            if (!mStop) {
                Log.d(TAG,"bindDeviceSuccess $name")
                mBindDeviceSuccess = true
                setAddDeviceName(name)
                showBindDeviceSuccessTip()
            }
        }
    }

    private fun deviceFind(gwId: String) {
        if (!mStop) {
            Log.d(TAG,"deviceFind  $gwId ")
            mvpView?.showDeviceFindTip(gwId)
        }
    }

    private fun checkLoop() {
        mvpView?.apply {
            if (mStop) return
            if (mTime >= 100) {
                Log.d(TAG,"checkLoop  $mTime ")
                stopSearch()
                configFailure()
            } else {
                Log.d(TAG,"checkLoop  22 ")
                setConnectProgress(mTime++.toFloat(), 1000)
                mHandler?.sendEmptyMessageDelayed(MESSAGE_CONFIG_WIFI_OUT_OF_TIME, 1000)
            }
        }
    }

    private fun configSuccess(deviceBean: DeviceBean?) {
        Log.d(TAG,"configSuccess  $deviceBean ")
        mvpView?.apply {
            if (deviceBean != null) {
                showToast(deviceBean)
            }
            stopSearch()
            showConfigSuccessTip()
            setConnectProgress(100f, 800)
            mHandler?.sendEmptyMessageDelayed(MESSAGE_SHOW_SUCCESS_PAGE, 1000)
        }
    }

    private fun stopSearch() {
        Log.d(TAG,"stopSearch ")
        mStop = true
        mHandler?.removeMessages(MESSAGE_CONFIG_WIFI_OUT_OF_TIME)
        mvpView?.cancel()
    }

    fun gotoShareActivity() {
        //        ActivityUtils.gotoActivity((Activity) mContext, SharedActivity.class, ActivityUtils.ANIMATE_FORWARD, true);
    }


    fun goForHelp() {
//        val intent = Intent(mContext, BrowserActivity::class.java)
//        intent.putExtra(BrowserActivity.EXTRA_LOGIN, false)
//        intent.putExtra(BrowserActivity.EXTRA_REFRESH, true)
//        intent.putExtra(BrowserActivity.EXTRA_TOOLBAR, true)
//        intent.putExtra(BrowserActivity.EXTRA_TITLE, mContext.getString(R.string.ty_ez_help))
//        intent.putExtra(BrowserActivity.EXTRA_URI, CommonConfig.FAILURE_URL)
//        mContext.startActivity(intent)
    }

    override fun handleMessage(msg: Message): Boolean {
        mvpView?.apply {
            when (msg.what) {
                MESSAGE_SHOW_SUCCESS_PAGE -> showSuccessPage()
                MESSAGE_CONFIG_WIFI_OUT_OF_TIME -> checkLoop()
                DeviceBindModel.WHAT_EC_GET_TOKEN_ERROR  -> {
                    stopSearch()
                    showNetWorkFailurePage()
                }
                DeviceBindModel.WHAT_EC_ACTIVE_ERROR -> {
                    L.d(TAG, "ec_active_error")
                    stopSearch()
                    if (mBindDeviceSuccess) {
                        showBindDeviceSuccessFinalTip()
                    }
                    showFailurePage()
                }
                DeviceBindModel.WHAT_AP_ACTIVE_ERROR -> {
                    L.d(TAG, "ap_active_error")
                    stopSearch()
                    if (mBindDeviceSuccess) {
                        showBindDeviceSuccessFinalTip()
                    }
                    showFailurePage()

                    if (BindDeviceUtils.isAPMode())
                        removeNetwork(getSSID())
                }

                DeviceBindModel.WHAT_EC_ACTIVE_SUCCESS  //EC激活成功
                    , DeviceBindModel.WHAT_AP_ACTIVE_SUCCESS  //AP激活成功
                -> {
                    L.d(TAG, "active_success")
                    val configDev = (msg.obj as Result).getObj() as DeviceBean
                    stopSearch()
                    configSuccess(configDev)
                }

                DeviceBindModel.WHAT_DEVICE_FIND -> {
                    L.d(TAG, "device_find")
                    deviceFind((msg.obj as Result).getObj() as String)
                }
                DeviceBindModel.WHAT_BIND_DEVICE_SUCCESS -> {
                    L.d(TAG, "bind_device_success")
                    bindDeviceSuccess(((msg.obj as Result).getObj() as GwDevResp).getName())
                }
            }
        }
        return super.handleMessage(msg)
    }

    override fun onDestroy() {
        L.d(TAG, "onDestroy")
        super.onDestroy()
        mHandler?.removeMessages(MESSAGE_CONFIG_WIFI_OUT_OF_TIME)
        mHandler?.removeMessages(MESSAGE_SHOW_SUCCESS_PAGE)
        mvpView?.destroy()
    }


}