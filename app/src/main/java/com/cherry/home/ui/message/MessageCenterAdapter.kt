package com.cherry.home.ui.message

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.cherry.home.ui.message.alarm.AlarmFragment
import com.cherry.home.ui.message.family.FamilyMessage
import com.cherry.home.ui.message.notification.NotificationFragment

class MessageCenterAdapter internal constructor(fragmentManager : FragmentManager, private var totalTabs : Int): FragmentPagerAdapter(fragmentManager) {

    private val ALARM : Int = 0
    private val FAMILY : Int = 1
    private val NOTIFICATION : Int = 2

    override fun getItem(position: Int): Fragment? {
        return when(position){
            ALARM ->{
                AlarmFragment()
            }
            FAMILY ->{
                FamilyMessage()
            }
            NOTIFICATION ->{
                NotificationFragment()
            }
            else ->{
                null
            }
        }
    }

    override fun getCount(): Int = totalTabs

}