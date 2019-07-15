package com.cherry.home.ui.family.empty

import com.cherry.home.ui.base.MvpView

interface EmptyFamilyView : MvpView {

    fun showError(error : String)

    fun showLoading()

    fun hideLoading()

    fun reLogin()
}