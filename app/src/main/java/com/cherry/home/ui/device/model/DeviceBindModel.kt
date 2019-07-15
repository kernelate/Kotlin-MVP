package com.cherry.home.ui.device.model

import android.content.Context
import com.cherry.home.ui.device.config.DeviceBindModelView
import com.tuya.smart.android.common.utils.SafeHandler
import com.tuya.smart.android.mvp.model.BaseModel
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorAPStepCode
import com.tuya.smart.sdk.enums.ActivatorEZStepCode
import com.tuya.smart.sdk.enums.ActivatorModelEnum
import com.tuya.smart.sdk.enums.ActivatorModelEnum.TY_AP
import com.tuya.smart.sdk.enums.ActivatorModelEnum.TY_EZ


class DeviceBindModel(ctx: Context, handler: SafeHandler) : BaseModel(ctx, handler), DeviceBindModelView {

    private var mTuyaActivator: ITuyaActivator? = null
    private var mModelEnum: ActivatorModelEnum? = null


    override fun start() {
        if (mTuyaActivator != null) {
            mTuyaActivator!!.start()
        }
    }

    override fun cancel() {
        if (mTuyaActivator != null) {
            mTuyaActivator!!.stop()
        }
    }

    override fun setEC(ssid: String, password: String, token: String) {
        mModelEnum = TY_EZ
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(ActivatorBuilder()
                .setSsid(ssid)
                .setContext(mContext)
                .setPassword(password)
                .setActivatorModel(TY_EZ)
                .setTimeOut(CONFIG_TIME_OUT)
                .setToken(token).setListener(object : ITuyaSmartActivatorListener {
                    override fun onError(s: String, s1: String) {
                        when (s) {
                            STATUS_FAILURE_WITH_GET_TOKEN -> {
                                resultError(WHAT_EC_GET_TOKEN_ERROR, "wifiError", s1)
                                return
                            }
                        }
                        resultError(WHAT_EC_ACTIVE_ERROR, s, s1)
                    }

                    override fun onActiveSuccess(gwDevResp: DeviceBean) {
                        resultSuccess(WHAT_EC_ACTIVE_SUCCESS, gwDevResp)
                    }

                    override fun onStep(s: String, o: Any) {
                        when (s) {
                            ActivatorEZStepCode.DEVICE_BIND_SUCCESS -> resultSuccess(WHAT_BIND_DEVICE_SUCCESS, o)
                            ActivatorEZStepCode.DEVICE_FIND -> resultSuccess(WHAT_DEVICE_FIND, o)
                        }
                    }
                }))
    }

    override fun setAP(ssid: String, password: String, token: String) {
        mModelEnum = TY_AP
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newActivator(ActivatorBuilder()
                .setSsid(ssid)
                .setContext(mContext)
                .setPassword(password)
                .setActivatorModel(TY_AP)
                .setTimeOut(CONFIG_TIME_OUT)
                .setToken(token).setListener(object : ITuyaSmartActivatorListener {
                    override fun onError(error: String, s1: String) {
                        resultError(WHAT_AP_ACTIVE_ERROR, error, s1)
                    }

                    override fun onActiveSuccess(gwDevResp: DeviceBean) {
                        resultSuccess(WHAT_AP_ACTIVE_SUCCESS, gwDevResp)
                    }

                    override fun onStep(step: String, o: Any) {
                        when (step) {
                            ActivatorAPStepCode.DEVICE_BIND_SUCCESS -> resultSuccess(WHAT_BIND_DEVICE_SUCCESS, o)
                            ActivatorAPStepCode.DEVICE_FIND -> resultSuccess(WHAT_DEVICE_FIND, o)
                        }
                    }
                }))

    }

    override fun configFailure() {
        if (mModelEnum == null) return
        if (mModelEnum == TY_AP) {
            //ap超时提示错误页面
            resultError(WHAT_AP_ACTIVE_ERROR, "TIME_ERROR", "OutOfTime")
        } else {
            //ez超时进入ap配网
            resultError(WHAT_EC_ACTIVE_ERROR, "TIME_ERROR", "OutOfTime")
        }
    }

    override fun onDestroy() {
        if (mTuyaActivator != null)
            mTuyaActivator!!.onDestroy()

    }

    companion object {
        val STATUS_FAILURE_WITH_NETWORK_ERROR = "1001"
        val STATUS_FAILURE_WITH_BIND_GWIDS = "1002"
        val STATUS_FAILURE_WITH_BIND_GWIDS_1 = "1003"
        val STATUS_FAILURE_WITH_GET_TOKEN = "1004"
        val STATUS_FAILURE_WITH_CHECK_ONLINE_FAILURE = "1005"
        val STATUS_FAILURE_WITH_OUT_OF_TIME = "1006"
        val STATUS_DEV_CONFIG_ERROR_LIST = "1007"
        val WHAT_EC_ACTIVE_ERROR = 0x02
        val WHAT_EC_ACTIVE_SUCCESS = 0x03
        val WHAT_AP_ACTIVE_ERROR = 0x04
        val WHAT_AP_ACTIVE_SUCCESS = 0x05
        val WHAT_EC_GET_TOKEN_ERROR = 0x06
        val WHAT_DEVICE_FIND = 0x07
        val WHAT_BIND_DEVICE_SUCCESS = 0x08
        private val CONFIG_TIME_OUT: Long = 100
    }
}