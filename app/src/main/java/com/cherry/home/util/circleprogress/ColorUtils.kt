package com.cherry.home.util.circleprogress

import android.graphics.Color
import android.support.annotation.ColorInt

object ColorUtils {

    fun getRGBGradient(@ColorInt startColor: Int, @ColorInt endColor: Int, proportion: Float): Int {

        val rgb = IntArray(3)
        rgb[0] = interpolate(Color.red(startColor).toFloat(), Color.red(endColor).toFloat(), proportion)
        rgb[1] = interpolate(Color.green(startColor).toFloat(), Color.green(endColor).toFloat(), proportion)
        rgb[2] = interpolate(Color.blue(startColor).toFloat(), Color.blue(endColor).toFloat(), proportion)
        return Color.argb(255, rgb[0], rgb[1], rgb[2])
    }


    private fun interpolate(a: Float, b: Float, proportion: Float): Int {
        return Math.round(a * proportion + b * (1 - proportion))
    }
}