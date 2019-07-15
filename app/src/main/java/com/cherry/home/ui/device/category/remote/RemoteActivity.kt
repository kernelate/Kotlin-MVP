package com.cherry.home.ui.device.category.remote

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.adapter.DeviceAdapter
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.view.DeviceView
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_remote.*
import javax.inject.Inject

class RemoteActivity : BaseActivity(), DeviceView, DeviceAdapter.ClickListener {

    @Inject lateinit var remotePresenter: RemotePresenter
    @Inject lateinit var deviceAdapter: DeviceAdapter

    override fun layoutId(): Int = R.layout.activity_remote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        remotePresenter.attachView(this)
        initToolbar()
        setToolbarTitle(R.string.device_remote)
    }


    override fun onClickDevice(device: String) {

    }

    override fun showCategories(categories: List<Category>) {

        deviceAdapter?.apply {

            setDevice(categories)
            notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        remotePresenter.detachView()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
            invalidate()
        }
    }

    override fun snackBarLayoutId(): ViewGroup = remote_id
}