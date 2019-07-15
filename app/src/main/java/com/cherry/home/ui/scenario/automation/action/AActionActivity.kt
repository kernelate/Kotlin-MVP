package com.cherry.home.ui.scenario.automation.action

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.function.AFunctionActivity
import com.cherry.home.util.ActivityUtils
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_select_action_auto.*
import javax.inject.Inject

class AActionActivity : BaseActivity(), AActionView, AActionAdapter.ClickListener {

    private val TAG : String = "AActionActivity"

    @Inject lateinit var aactionPresenter: AActionPresenter

    @Inject lateinit var aactionAdapter: AActionAdapter

    companion object {
        fun gotoAutoActionActivityForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, AActionActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        aactionPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.select_action)

        recyclerView_auto_action?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = aactionAdapter
        }

        aactionAdapter.setClickListener(this)
    }

    override fun layoutId(): Int = R.layout.activity_select_action_auto

    override fun snackBarLayoutId(): ViewGroup = select_action_auto_id

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume() {
        super.onResume()
        aactionPresenter.init()
        aactionPresenter.getDataFromServer()

    }

    override fun onClickDevice(device: DeviceBean) {
        val i = Intent(this, AFunctionActivity::class.java)
        i.putExtra("devId", device.devId)
        i.putExtra("requestCode", Constant.REQUEST_SCENE_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, true)
    }

    override fun updateDeviceData(myDevices: List<DeviceBean>) {
        if (aactionAdapter != null){
            aactionAdapter.apply {
                setData(myDevices)
                notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        aactionPresenter.detachView()
    }

}