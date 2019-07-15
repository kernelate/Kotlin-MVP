package com.cherry.home.ui.device.setting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.location.DeviceLocationActivity
import com.cherry.home.ui.device.sharing.DeviceSharingActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.DialogUtil
import com.cherry.home.util.ProgressUtil
import kotlinx.android.synthetic.main.activity_device_setting.*
import javax.inject.Inject

class DeviceSettingActivity : BaseActivity(), DeviceSettingView {

    private val TAG : String = "DeviceSettingActivity"

    @Inject lateinit var deviceSettingPresenter: DeviceSettingPresenter
    private lateinit var devId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        deviceSettingPresenter.attachView(this)

        if(savedInstanceState == null){
            devId = intent.getStringExtra("devId")

        }
        Log.d(TAG, "onCreate $devId")

        initToolbar()
        setToolbarTitle(R.string.device_details)

        deviceSettingPresenter.initData(devId)
        onClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun layoutId(): Int = R.layout.activity_device_setting

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(code: String, msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun dialogSettingRemoveDevice() {
        DialogUtil.simpleConfirmDialog(this, this.getString(R.string.device_confirm_remove), DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                ProgressUtil.showLoading(this, "removing device...")
                deviceSettingPresenter.unBind(devId!!)
                dialog.dismiss()
            }
        })
    }

    override fun dialogRenameDevice() {
        DialogUtil.simpleInputDialog(this, this.getString(R.string.rename), getDeviceName(), false, object : DialogUtil.SimpleInputDialogInterface {
            override fun onPositive(dialog: DialogInterface, inputText: String) {
//                val limit = this.getResources().getInteger(R.integer.change_device_name_limit)
                if (inputText.length > 25) {
//                    ToastUtil.showToast(this, R.string.ty_modify_device_name_length_limit)
                } else {
                    renameDevice(inputText)
                }
            }

            override fun onNegative(dialog: DialogInterface) {

            }
        })
    }

    override fun snackBarLayoutId(): ViewGroup = device_setting_id

    fun onClick(){
        device_setting_remove.setOnClickListener {
//            deviceSettingPresenter.unBind(devId!!)
            dialogSettingRemoveDevice()
        }

        ll_info.setOnClickListener {
            dialogRenameDevice()
        }

        deviceloc_linear.setOnClickListener {
//            val i = Intent(this, DeviceLocationActivity::class.java)
//
//            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, false)
            DeviceLocationActivity.gotoDeviceLocationForResult(this, DeviceSettingPresenter.REQUEST_LOCATION)
        }

        device_setting_share.setOnClickListener {
            val i = Intent(this, DeviceSharingActivity::class.java)
            i.putExtra("devId", devId)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun renameDevice(titleName: String) {
        deviceSettingPresenter.renameDevice(titleName)
    }

    override fun setDeviceName(devName: String) {
        device_setting_name.text = devName
    }

    override fun setDeviceLocation(room: String) {
        tvLocation.text = room
    }

    override fun gotoMainActivity() {
        ActivityUtils.gotoMainActivity(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        deviceSettingPresenter.onActivityResult(requestCode, resultCode,data)
    }

    private fun getDeviceName() = device_setting_name.text.toString()

}