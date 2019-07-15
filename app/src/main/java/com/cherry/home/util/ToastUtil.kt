package com.cherry.home.util

import android.content.Context
import android.widget.Toast

object ToastUtil {

    private var longToast: Toast? = null
    private var shortToast: Toast? = null

    @Synchronized
    fun showToast(context: Context, resId: Int) {
        showToast(context, context.getString(resId))
    }

    @Synchronized
    fun showToast(context: Context, tips: String) {
        if (longToast == null) {
            longToast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        }

        longToast!!.setText(tips)
        longToast!!.show()
    }

    @Synchronized
    fun shortToast(context: Context, tips: String) {
        if (shortToast == null) {
            shortToast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        }
        shortToast!!.setText(tips)
        shortToast!!.show()
    }

    @Synchronized
    fun shortToast(context: Context, tipsResId: Int) {
        showToast(context, context.getString(tipsResId))
    }

}