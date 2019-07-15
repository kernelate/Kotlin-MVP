package com.cherry.home.app

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.ArrayList

object Constant {

    internal var HOME_ID: Long = 1099001
    internal var UID : String = "uid"
    internal var USERNAME : String = "username"
    internal var CONFIG_PASSWORD : String = "config_password"
    internal var CONFIG_SSID: String = "config_ssid"
    internal var ACCESS_TOKEN : String = "access_token"
    internal var EXPIRE_TIME : Long = 0
    internal var REFRESH_TOKEN : String? = "refresh_token"
    internal var SCHEMA : String = "cherryhome"
    internal var PH_COUNTRY_CODE : String = "63"
    internal var SIGNED : String = "signed"
    internal var SCENE_COVER : String = "scene_cover"

    internal var LIVING_ROOM : String = "Living Room"
    internal var BED_ROOM : String = "Bedroom"
    internal var DINING_ROOM : String = "Dining Room"
    internal var KITCHEN : String = "Kitchen"

    private val activityStack = ArrayList<WeakReference<Activity>>()

    val TAG = "cherryHome"

    internal val MODE_REGISTER = "register"
    internal val MODE_LOGIN = "login"
    internal val MODE_FORGOT_PASSWORD = "forgot"
    internal val MODE_ACTIVATE = "activate"
    internal val MODE_ADD_FAMILY = "addFamily"
    internal val MODE_SIGN = "sign"

    internal val PREF_NAME = "cherry_home"
    internal val CONFIG_MODE = "config_mode"
    internal val EASY_SIGN = 1
    internal val HMAC_SHA256 = "HMAC-SHA256"
    internal val FORM_URL_ENCODED = "application/x-www-form-urlencoded"
    internal val APPLICATION_JSON = "application/json"

    internal val PROC_FOR_CODE = "forCode"
    internal val PROC_FOR_EMAIL = "forEmail"
    internal val PROC_FOR_PASSWORD = "forNewPassword"

    internal var MODE = 1

    internal var REQUEST_SCENE_TASK = 9
    internal var REQUEST_MODIFY_SCENETASK = 8
    internal var REQUEST_ADD_SCENE = 7

    internal var REQUEST_SCENE_AUTO = 10
    internal var REQUEST_MODIFY_AUTO = 11
    internal var REQUEST_ADD_AUTO = 12
    internal val REQUEST_LOCATION = 2

    internal val roomlist = listOf("Living Room", "Dining Room", "Bedroom", "KitchenFragment")

    fun exitApplication() {
        finishActivity()
        //        SyncServerTask.getInstance().onDestroy();
    }

    fun detachActivity(activity: Activity) {
        val act = WeakReference(activity)
        activityStack.remove(act)
    }

    fun finishActivity() {
        for (activity in activityStack) {
            if (activity.get() != null) activity.get()!!.finish()
        }
        activityStack.clear()
    }

    fun finishLastActivity(finishNum: Int) {
        if (activityStack == null) return
        var num = 1
        val activityStacks = ArrayList<WeakReference<Activity>>()
        val size = activityStack.size
        for (i in size - 1 downTo 0) {
            val activity = activityStack[i]
            if (activity != null && activity.get() != null) {
                activity.get()!!.finish()
                activityStacks.add(activity)
                if (num++ == finishNum) break
            }
        }

        for (activity in activityStacks) {
            activityStack.remove(activity)
        }
        activityStacks.clear()
    }

    fun attachActivity(activity: Activity) {
        val act = WeakReference(activity)
        if (activityStack.indexOf(act) == -1) activityStack.add(act)
    }
}