package com.cherry.home.ui.device.add

import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.ui.device.view.DeviceView
import javax.inject.Inject

@ConfigPersistent
class AddDevicePresenter @Inject constructor() : BasePresenter<DeviceView>() {

    val categories = arrayOf("Socket", "Switch", "Sensor", "Remote")
    val categoryIcon = intArrayOf(R.drawable.category_socket, R.drawable.category_switch, R.drawable.category_sensor, R.drawable.category_remote)

    fun populateList(){
        checkViewAttached()
        val list = ArrayList<Category>()

        for (i in 0..3) {
            val imageModel = Category(categories[i], categoryIcon[i])
            list.add(imageModel)
        }
        mvpView?.showCategories(list)
    }


}