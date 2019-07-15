package com.cherry.home.ui.device.category.socket

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.adapter.DeviceAdapter
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.view.DeviceView
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_socket.*
import javax.inject.Inject

class ListSocketActivity : BaseActivity(), DeviceView, DeviceAdapter.ClickListener {

    @Inject lateinit var listSocketPresenter: ListSocketPresenter
    @Inject lateinit var deviceAdapter: DeviceAdapter

    override fun layoutId(): Int = R.layout.activity_socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        listSocketPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.device_socket)
        listSocketPresenter.populateList()

        deviceAdapter.setClickListener(this)


    }

    override fun onResume() {
        super.onResume()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
        }
    }


    override fun onClickDevice(device: String) {
        startWifiDevConfig()

    }

    override fun showCategories(categories: List<Category>) {
        deviceAdapter?.apply {
            setDevice(categories)
            notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listSocketPresenter.detachView()
    }

    override fun snackBarLayoutId(): ViewGroup = socket_id

     private fun startWifiDevConfig() {
        ActivityUtils.gotoActivity(this, AddDeviceTipActivity::class.java, ActivityUtils.ANIMATE_FORWARD, false)
    }
}