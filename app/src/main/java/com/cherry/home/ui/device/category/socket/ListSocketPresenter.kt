package com.cherry.home.ui.device.category.socket

import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.device.view.DeviceView
import javax.inject.Inject

@ConfigPersistent
class ListSocketPresenter @Inject constructor() : BasePresenter<DeviceView>() {

    val listSocket = arrayOf("Wall Socket", "Smart Socket","Smart Power Strip")
    val listSocketIcon = intArrayOf(R.drawable.socket_electrical, R.drawable.socket_smart, R.drawable.socket_powerstrip)

    fun populateList(){
        checkViewAttached()
        val list = ArrayList<Category>()

        for (i in 0..2) {
            val imageModel = Category(listSocket[i], listSocketIcon[i])
            list.add(imageModel)
        }
        mvpView?.showCategories(list)
    }
}