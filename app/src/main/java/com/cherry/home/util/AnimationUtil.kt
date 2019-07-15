package com.cherry.home.util

import android.view.View
import android.view.animation.*

object AnimationUtil {
    val DEFAULT_SLIDE_DURATION = 300

    fun fadeInItem(view: View, duration: Int, startOffset: Int, visibility: Int, listener: Animation.AnimationListener) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        startAnimation(view, anim, visibility, listener)
    }

    fun fadeInItems(duration: Int, startOffset: Int, visibility: Int, views: Array<View>) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        for (j in views.indices) {
            startAnimation(views[j], anim, visibility, null)
        }
    }

    fun fadeItem(view: View, duration: Int, startOffset: Int, visibility: Int, listener: Animation.AnimationListener) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        startAnimation(view, anim, visibility, listener)
    }

    fun fadeItems(duration: Int, startOffset: Int, visibility: Int, views: Array<View>) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        for (j in views.indices) {
            startAnimation(views[j], anim, visibility, null)
        }
    }

    fun getAbsoluteHorizontalSlideAnimation(fromXValue: Float, toXValue: Float, duration: Int): Animation {
        val anim = TranslateAnimation(Animation.ABSOLUTE, fromXValue, Animation.ABSOLUTE, toXValue,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        anim.duration = duration.toLong()
        return anim
    }

    fun getHorizontalSlideAnimation(fromXValue: Float, toXValue: Float, duration: Int): Animation {
        val anim = TranslateAnimation(Animation.RELATIVE_TO_SELF, fromXValue,
                Animation.RELATIVE_TO_SELF, toXValue, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f)
        anim.duration = duration.toLong()
        return anim
    }

    fun getIdentityAnimation(): Animation {
        val anim = TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f)
        anim.duration = 1L
        return anim
    }

    fun getOffsetHorizontalSlideAnimation(fromYValue: Int, fromXValue: Int, toXValue: Int,
                                          duration: Int, startOffset: Int): Animation {
        val anim = TranslateAnimation(Animation.ABSOLUTE, fromXValue.toFloat(), Animation.ABSOLUTE,
                toXValue.toFloat(), Animation.ABSOLUTE, fromYValue.toFloat(), Animation.ABSOLUTE, fromYValue.toFloat())
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        anim.interpolator = DecelerateInterpolator()
        return anim
    }

    fun getOffsetVerticalSlideAnimation(fromXValue: Int, fromYValue: Int, toYValue: Int, duration: Int,
                                        startOffset: Int): Animation {
        val anim = TranslateAnimation(Animation.ABSOLUTE, fromXValue.toFloat(), Animation.ABSOLUTE,
                fromXValue.toFloat(), Animation.ABSOLUTE, fromYValue.toFloat(), Animation.ABSOLUTE, toYValue.toFloat())
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        return anim
    }

    fun getRelativeHorizontalSlideAnimation(fromXValue: Float, toXValue: Float, duration: Int): Animation {
        val anim = TranslateAnimation(Animation.RELATIVE_TO_SELF, fromXValue,
                Animation.RELATIVE_TO_SELF, toXValue, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f)
        anim.duration = duration.toLong()
        return anim
    }

    fun getRelativeHorizontalSlideAnimation(fromXValue: Float, toXValue: Float): Animation {
        return getRelativeHorizontalSlideAnimation(fromXValue, toXValue, DEFAULT_SLIDE_DURATION)
    }

    fun getRelativeVerticalSlideAnimation(fromYValue: Float, toYValue: Float, duration: Int): Animation {
        val anim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, fromYValue, Animation.RELATIVE_TO_SELF, toYValue)
        anim.duration = duration.toLong()
        return anim
    }

    fun getRelativeVerticalSlideAnimation(fromYValue: Float, toYValue: Float): Animation {
        return getRelativeVerticalSlideAnimation(fromYValue, toYValue, DEFAULT_SLIDE_DURATION)
    }

    fun getVerticalSlideAnimation(fromYPos: Int, toYPos: Int, duration: Int, startOffset: Int): Animation {
        val anim = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.ABSOLUTE, fromYPos.toFloat(), Animation.ABSOLUTE, toYPos.toFloat())
        anim.duration = duration.toLong()
        anim.startOffset = startOffset.toLong()
        return anim
    }

    fun getAlphaAnimation(fromAlpha: Float, toAlpha: Float, duration: Int): Animation {
        val alphaAnim = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnim.duration = duration.toLong()
        return alphaAnim
    }

    fun getScaleAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float, duration: Int): Animation {
        val scaleAnim = ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = duration.toLong()
        return scaleAnim
    }

    fun getRotateAnimation(fromDegrees: Float, toDegrees: Float, pivotX: Float, pivotY: Float,
                           duration: Int, repeatCount: Int, interpolator: Interpolator?, fillAfter: Boolean): Animation {
        val anim = RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, pivotX,
                Animation.RELATIVE_TO_SELF, pivotY)
        anim.repeatCount = repeatCount
        anim.duration = duration.toLong()
        anim.interpolator = interpolator ?: LinearInterpolator()
        anim.fillAfter = fillAfter
        return anim
    }

    fun getRelativeToParetnVerticalSlideAnimation(fromX: Float, toX: Float, fromY: Float, toY: Float,
                                                  duration: Int, startOff: Long): Animation {
        val verticalSlideAnimation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromX,
                Animation.RELATIVE_TO_PARENT, toX, Animation.RELATIVE_TO_PARENT, fromY, Animation.RELATIVE_TO_PARENT,
                toY)
        verticalSlideAnimation.duration = duration.toLong()
        verticalSlideAnimation.startOffset = startOff
        return verticalSlideAnimation

    }

    fun startAnimation(view: View?, anim: Animation, visibility: Int, listener: Animation.AnimationListener?) {
        if (listener != null) {
            anim.setAnimationListener(listener)
        }
        if (view != null) { // 此处出现空指针异常.
            view.startAnimation(anim)
            view.visibility = visibility

        }
    }

    fun AlphaView(view: View, fromAlpha: Float, toAlpha: Float, durationMillis: Long, fillAfter: Boolean,
                  listener: Animation.AnimationListener) {
        val anim = AlphaAnimation(fromAlpha, toAlpha)
        anim.duration = durationMillis
        anim.fillAfter = fillAfter
        anim.setAnimationListener(listener)
        view.startAnimation(anim)
    }

    fun scaleView(view: View, fromX: Float, toX: Float, fromY: Float, toY: Float, durationMillis: Long,
                  fillAfter: Boolean, listener: Animation.AnimationListener) {
        val anim = ScaleAnimation(fromX, toX, fromY, toY, 1, 0.5f, 1, 0.5f)
        anim.duration = durationMillis
        anim.fillAfter = fillAfter
        anim.setAnimationListener(listener)
        view.startAnimation(anim)
    }

    fun animateView(view: View, fromXDelta: Float, toXDelta: Float, fromYDelta: Float, toYDelta: Float,
                    durationMillis: Long, fillAfter: Boolean, listener: Animation.AnimationListener) {
        val localTranslateAnimation = TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta)
        localTranslateAnimation.duration = durationMillis
        localTranslateAnimation.fillAfter = fillAfter
        localTranslateAnimation.setAnimationListener(listener)
        view.startAnimation(localTranslateAnimation)
    }

    fun translateView(view: View, dxFrom: Float, dxTo: Float, dyFrom: Float, dyTo: Float, duration: Long,
                      fillafter: Boolean, animListener: Animation.AnimationListener?) {
        val anim = TranslateAnimation(dxFrom, dxTo, dyFrom, dyTo)
        anim.duration = duration
        anim.fillAfter = fillafter
        anim.setAnimationListener(animListener)
        view.startAnimation(anim)
    }

    fun translateView(view: View, fromXType: Int, fromXValue: Float, toXType: Int, toXValue: Float,
                      fromYType: Int, fromYValue: Float, toYType: Int, toYValue: Float, duration: Long, fillafter: Boolean,
                      animListener: Animation.AnimationListener?) {
        val anim = TranslateAnimation(fromXType, fromXValue, toXType, toXValue, fromYType,
                fromYValue, toYType, toYValue)
        anim.duration = duration
        anim.fillAfter = fillafter
        anim.setAnimationListener(animListener)
        view.startAnimation(anim)
    }
}