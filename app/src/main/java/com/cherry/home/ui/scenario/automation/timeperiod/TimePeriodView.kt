package com.cherry.home.ui.scenario.autosettings.timeperiod


import com.cherry.home.ui.base.MvpView

interface TimePeriodView : MvpView{


    fun showProgress(show: Boolean)

    fun showError(error: String)

    fun setLocation(location : String)

    fun getCityName(latitude : Double, longitude : Double)
}