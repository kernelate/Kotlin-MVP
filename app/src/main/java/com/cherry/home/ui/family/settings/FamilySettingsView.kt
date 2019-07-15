package com.cherry.home.ui.family.settings

import com.cherry.home.ui.base.MvpView
import com.tuya.smart.home.sdk.bean.MemberBean

interface FamilySettingsView : MvpView {

    fun showError(error : String)

    fun setFamilyName(name : String)

    fun setLocation(location : String)

    fun getMember(memberBeanList: MutableList<MemberBean>)

    fun goToHomeManagement()

}