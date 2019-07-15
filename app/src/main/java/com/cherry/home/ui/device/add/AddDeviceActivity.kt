package com.cherry.home.ui.device.add

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.data.model.Category
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.adapter.DeviceAdapter
import com.cherry.home.ui.device.category.remote.RemoteActivity
import com.cherry.home.ui.device.category.sensor.SensorActivity
import com.cherry.home.ui.device.category.smartswitch.SwitchActivity
import com.cherry.home.ui.device.category.socket.ListSocketActivity
import com.cherry.home.ui.device.config.AddDeviceTipActivity
import com.cherry.home.ui.device.view.DeviceView
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import kotlinx.android.synthetic.main.activity_add_device.*
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import javax.inject.Inject


class AddDeviceActivity : BaseActivity(), DeviceView, DeviceAdapter.ClickListener {

    @Inject lateinit var addDevicePresenter: AddDevicePresenter
    @Inject lateinit var deviceAdapter: DeviceAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        addDevicePresenter.attachView(this)
        initToolbar()
        setToolbarTitle(R.string.add_device)
        addDevicePresenter.populateList()

        deviceAdapter.setClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
        }
    }

    override fun showCategories(categories: List<Category>) {
        deviceAdapter?.apply {
            setDevice(categories)
            notifyDataSetChanged()
        }
    }

    override fun layoutId(): Int = R.layout.activity_add_device


    override fun onClickDevice(device: String) {

        when(device)
        {
            "Socket" -> {
                val i = Intent(this, ListSocketActivity::class.java)
                ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
            }

            "Switch" -> {
                val i = Intent(this, SwitchActivity::class.java)
                ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
            }

            "Sensor" -> {
                startWifiDevConfig()
            }

            "Remote" -> {
                startWifiDevConfig()
            }
        }

        recyclerView?.apply {
            layoutManager = null
            adapter = null
            invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        recyclerView?.apply {
            layoutManager = null
            adapter = null
            invalidate()
        }

        addDevicePresenter.detachView()
    }

    private fun startWifiDevConfig() {
        ActivityUtils.gotoActivity(this, AddDeviceTipActivity::class.java, ActivityUtils.ANIMATE_FORWARD, false)
    }

    override fun snackBarLayoutId(): ViewGroup = add_device_id

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        super.onNetworkConnectionChanged(isConnected)

        when(isConnected){
            TRUE ->{
                recyclerView.visible()
            }

            FALSE ->{
                recyclerView.gone()
            }
        }
    }

}