package com.cherry.home.util

import android.app.Activity
import android.app.LauncherActivity
import android.content.Intent
import com.cherry.home.R
import com.cherry.home.ui.main.MainActivity

object ActivityUtils {
    //动画标识
    val ANIMATE_NONE = -1
    val ANIMATE_FORWARD = 0
    val ANIMATE_BACK = 1
    val ANIMATE_EASE_IN_OUT = 2
    val ANIMATE_SLIDE_TOP_FROM_BOTTOM = 3
    val ANIMATE_SLIDE_BOTTOM_FROM_TOP = 4
    val ANIMATE_SCALE_IN = 5
    val ANIMATE_SCALE_OUT = 6

    fun gotoActivity(from: Activity, clazz: Class<out Activity>?, direction: Int, finished: Boolean) {
        if (clazz == null) return
        val intent = Intent()
        intent.setClass(from, clazz)
        startActivity(from, intent, direction, finished)
    }

    fun gotoLauncherActivity(activity: Activity, direction: Int, finished: Boolean) {
        val intent = Intent(activity, LauncherActivity::class.java)
        startActivity(activity, intent, direction, finished)
    }

    fun startActivity(activity: Activity?, intent: Intent, direction: Int, finishLastActivity: Boolean) {
        if (activity == null) return
        activity.startActivity(intent)
        if (finishLastActivity) activity.finish()
        overridePendingTransition(activity, direction)
    }

    fun startActivityForResult(activity: Activity?, intent: Intent, backCode: Int, direction: Int, finishLastActivity: Boolean) {
        if (activity == null) return
        activity.startActivityForResult(intent, backCode)
        if (finishLastActivity) activity.finish()
        overridePendingTransition(activity, direction)
    }

    fun back(activity: Activity) {
        activity.finish()
        overridePendingTransition(activity, ANIMATE_BACK)
    }

    fun back(activity: Activity, direction: Int) {
        activity.finish()
        overridePendingTransition(activity, direction)
    }

    fun overridePendingTransition(activity: Activity, direction: Int) {
        if (direction == ANIMATE_FORWARD) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (direction == ANIMATE_BACK) {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        } else if (direction == ANIMATE_EASE_IN_OUT) {
            activity.overridePendingTransition(R.anim.easein, R.anim.easeout)
        } else if (direction == ANIMATE_SLIDE_TOP_FROM_BOTTOM) {
            activity.overridePendingTransition(R.anim.slide_bottom_to_top, R.anim.slide_none_medium_time)
        } else if (direction == ANIMATE_SLIDE_BOTTOM_FROM_TOP) {
            activity.overridePendingTransition(R.anim.slide_none_medium_time, R.anim.slide_top_to_bottom)
        } else if (direction == ANIMATE_SCALE_IN) {
            activity.overridePendingTransition(R.anim.popup_scale_in, R.anim.slide_none)
        } else if (direction == ANIMATE_SCALE_OUT) {
            activity.overridePendingTransition(R.anim.slide_none, R.anim.popup_scale_out)
        } else if (direction == ANIMATE_NONE) {
            //do nothing
        } else {
            //            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.slide_none);
            activity.overridePendingTransition(R.anim.magnify_fade_in, R.anim.fade_out)
        }
    }

    fun gotoMainActivity(context: Activity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(context, intent, ANIMATE_NONE, true)
    }

    fun gotoAddDeviceHelpActivity(activity: Activity, title: String) {
//        val intent = Intent(activity, BrowserActivity::class.java)
//        intent.putExtra(BrowserActivity.EXTRA_LOGIN, false)
//        intent.putExtra(BrowserActivity.EXTRA_REFRESH, true)
//        intent.putExtra(BrowserActivity.EXTRA_TOOLBAR, true)
//        intent.putExtra(BrowserActivity.EXTRA_TITLE, title)
//
//        val a = activity.obtainStyledAttributes(intArrayOf(R.attr.is_add_device_help_get_from_native))
//        val isAddDeviceHelpAsset = a.getBoolean(0, false)
//        if (isAddDeviceHelpAsset) {
//            val isChinese = TuyaUtil.isZh(TuyaSdk.getApplication())
//            if (isChinese) {
//                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_cn.html")
//            } else {
//                intent.putExtra(BrowserActivity.EXTRA_URI, "file:///android_asset/add_device_help_en.html")
//            }
//        } else {
//            intent.putExtra(BrowserActivity.EXTRA_URI, CommonConfig.RESET_URL)
//        }
//        a.recycle()
//
//        activity.startActivity(intent)
    }

}