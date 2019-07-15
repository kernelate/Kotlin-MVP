package com.cherry.home.ui.profile.timezone

import com.cherry.home.ui.base.MvpView

interface TimeZoneView : MvpView {

    fun getTimeZone(timeZone : List<String>)
}