package com.cherry.home.ui.device.view

import com.cherry.home.data.model.Category
import com.cherry.home.data.model.NoImageList
import com.cherry.home.ui.base.MvpView

interface DeviceView : MvpView {

    fun showCategories(categories: List<Category>)

}