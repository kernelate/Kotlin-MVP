package com.cherry.home.ui.device.location

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.util.ActivityUtils
import com.tuya.smart.home.sdk.bean.RoomBean
import kotlinx.android.synthetic.main.activity_device_location.*
import kotlinx.android.synthetic.main.activity_device_setting.*
import javax.inject.Inject

class DeviceLocationActivity : BaseActivity(), DeviceLocationView, DeviceLocationAdapter.ClickListener {

    private val TAG : String = "DeviceLocationActivity"

    @Inject lateinit var deviceLocationPresenter: DeviceLocationPresenter
    @Inject lateinit var deviceLocationAdapter: DeviceLocationAdapter

    companion object {
        fun gotoDeviceLocationForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, DeviceLocationActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, 0, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        deviceLocationPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.device_setting_location)

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceLocationAdapter
        }

        deviceLocationPresenter.getHomeRoomList()
        deviceLocationAdapter.setClickListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_smart_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId){
        R.id.save_smart_settings ->{
            val room = deviceLocationPresenter.getRoom()
            if (room != null){
                saveRoom(room)
            } else {
                Toast.makeText(this,"Please select a room.", Toast.LENGTH_SHORT).show()
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun layoutId(): Int = R.layout.activity_device_location

    override fun snackBarLayoutId(): ViewGroup = device_setting_id

    override fun showError(error_msg: String) {
        showSnack(snackBarLayoutId(), error_msg, Snackbar.LENGTH_SHORT, null)
    }

    override fun onClickItem(item: RoomBean) {
        deviceLocationPresenter.setRoom(item)
    }

    override fun getRoomList(roomList: MutableList<RoomBean>) {
        deviceLocationAdapter?.apply {
            setItem(roomList)
            notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deviceLocationPresenter.detachView()
    }
    private fun saveRoom(roomBean: RoomBean){
        val intent = Intent()
        intent.putExtra("name", roomBean.name)
        intent.putExtra("id", roomBean.roomId)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

}