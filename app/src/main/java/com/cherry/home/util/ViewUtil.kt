package com.company.project.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.TextView

object ViewUtil {

    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return px / (densityDpi / 160f)
    }

    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return Math.round(dp * density)
    }

    fun setTextViewDrawableLeft(context: Context, textView: TextView, drawId: Int) {
        val drawable: Drawable?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.resources.getDrawable(drawId, null)
        } else {
            drawable = context.resources.getDrawable(drawId)
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            textView.setCompoundDrawables(drawable, null, null, null)
        }
    }

    fun getColor(context: Context, color: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(color)
        } else {
            context.resources.getColor(color)
        }
    }



}
