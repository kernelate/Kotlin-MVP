package com.cherry.home.util

import android.os.Message
import com.tuya.smart.android.mvp.bean.Result
import com.tuya.smart.android.network.http.BusinessResponse

object MessageUtil {

    fun getCallFailMessage(msgWhat: Int, businessResponse: BusinessResponse): Message {
        return getCallFailMessage(msgWhat, businessResponse.errorCode, businessResponse.errorMsg)
    }

    fun getCallFailMessage(msgWhat: Int, errorCode: String, errorMsg: String): Message {
        val msg = Message()
        msg.what = msgWhat
        val result = Result()
        result.error = errorMsg
        result.errorCode = errorCode
        msg.obj = result
        return msg
    }

    fun getCallFailMessage(msgWhat: Int, errorCode: String, errorMsg: String, resultObj: Any): Message {
        val msg = Message()
        msg.what = msgWhat
        val result = Result()
        result.error = errorMsg
        result.errorCode = errorCode
        result.setObj(resultObj)
        msg.obj = result

        return msg
    }


    fun getCallFailMessage(msgWhat: Int, businessResponse: BusinessResponse, resultObj: Any): Message {
        return getCallFailMessage(msgWhat, businessResponse.errorCode, businessResponse.errorMsg, resultObj)
    }

    fun getMessage(msgWhat: Int, msgObj: Any): Message {
        val msg = Message()
        msg.what = msgWhat
        msg.obj = msgObj
        return msg
    }

    fun getResultMessage(msgWhat: Int, msgObj: Any): Message {
        val msg = Message()
        msg.what = msgWhat
        val result = Result()
        result.setObj(msgObj)
        msg.obj = result
        return msg
    }
}