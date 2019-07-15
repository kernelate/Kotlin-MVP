package com.cherry.home.ui.device.sharing

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.tuya.smart.home.sdk.bean.SharedUserInfoBean
import kotlinx.android.synthetic.main.activity_device_sharing.*
import kotlinx.android.synthetic.main.add_sharing_dialog.view.*
import javax.inject.Inject

class DeviceSharingActivity : BaseActivity(), DeviceSharingView {

    @Inject lateinit var deviceSharingPresenter: DeviceSharingPresenter
    @Inject lateinit var deviceSharingAdapter: DeviceSharingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        deviceSharingPresenter.attachView(this)

        var devId = intent.getStringExtra("devId")

        initToolbar()
        setToolbarTitle(R.string.share_device)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceSharingAdapter
        }

        deviceSharingPresenter.init(devId)
        deviceSharingPresenter.getSharedUserList()

        onClick()
    }

    override fun layoutId(): Int = R.layout.activity_device_sharing

    override fun snackBarLayoutId(): ViewGroup = rl_device_sharing

    override fun showError(error: String) {
        showSnack(snackBarLayoutId(), error, Snackbar.LENGTH_SHORT, null)
    }

    override fun getSharedUserList(sharedUserList: List<SharedUserInfoBean>) {
        deviceSharingAdapter.getSharedUser(sharedUserList)
    }

    private fun onClick(){
        add_sharing.setOnClickListener {
            showAddSharing()
        }
    }

    private fun showAddSharing() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_sharing_dialog, null)

        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Add Sharing")


        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogAdd.setOnClickListener {
            mAlertDialog.dismiss()

        }

        mDialogView.dialogCancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}