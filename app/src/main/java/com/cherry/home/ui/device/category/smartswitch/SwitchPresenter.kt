package com.cherry.home.ui.device.category.smartswitch

import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.device.view.DeviceView
import javax.inject.Inject

@ConfigPersistent
class SwitchPresenter @Inject constructor() : BasePresenter<DeviceView>() {

    val listSocket = arrayOf("Wall Switch (1 gang)", "Wall Switch (3 gang)", "Wall Switch (glass)")
    val listSocketIcon = intArrayOf(R.drawable.wallswitch1, R.drawable.wallswitch3, R.drawable.socket_smart)

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