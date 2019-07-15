package com.cherry.home.ui.device.category.sensor

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
import kotlinx.android.synthetic.main.activity_sensor.*
import javax.inject.Inject

class SensorActivity : BaseActivity(), DeviceView, DeviceAdapter.ClickListener {

    @Inject lateinit var sensorPresenter: SensorPresenter
    @Inject lateinit var deviceAdapter: DeviceAdapter

    private var TAG : String = "SensorActivity"

    override fun layoutId(): Int = R.layout.activity_sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        activityComponent().inject(this)
        sensorPresenter.attachView(this)
        initToolbar()
        setToolbarTitle(R.string.device_sensor)
    }

    override fun onClickDevice(device: String) {
        Log.d(TAG, "onClickDevice")
    }

    override fun showCategories(categories: List<Category>) {

    }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    override fun snackBarLayoutId(): ViewGroup = sensor_id
}