package com.cherry.home.ui.profile.timezone

import android.util.Log
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import java.util.*
import javax.inject.Inject

@ConfigPersistent
class TimeZonePresenter @Inject constructor() : BasePresenter<TimeZoneView>(){

    private var listTimeZone : List<String>? = null

    fun getTimeZone(){
        checkViewAttached()
        listTimeZone = TimeZone.getAvailableIDs().toList()
        mvpView?.getTimeZone(listTimeZone!!)
    }

    fun filter(query : String){
        val textlength = query.length
        var arrayList = ArrayList<String>()
        for (i in 0 until listTimeZone!!.size) {
            if (textlength <= listTimeZone!![i].length) {
                if (listTimeZone!![i].toLowerCase().trim().contains(
                                query.toLowerCase().trim())) {
                    arrayList.add(listTimeZone!![i])
                }
            }
        }
        mvpView?.getTimeZone(arrayList)
    }
}