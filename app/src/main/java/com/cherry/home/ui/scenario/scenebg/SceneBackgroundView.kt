package com.cherry.home.ui.scenario.scenebg

import com.cherry.home.ui.base.MvpView

interface SceneBackgroundView : MvpView {

    fun showError(error_msg : String)

    fun updateImageView(bgList : ArrayList<String>)
}