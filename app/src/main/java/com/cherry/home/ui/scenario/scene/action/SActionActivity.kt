package com.cherry.home.ui.scenario.scene.action

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.scene.function.SFunctionActivity
import com.cherry.home.util.ActivityUtils
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_select_action.*
import javax.inject.Inject

class SActionActivity : BaseActivity(), SActionView, SActionAdapter.ClickListener {

    private val TAG : String = "SActionActivity"

    @Inject lateinit var sactionAdapter: SActionAdapter

    @Inject lateinit var sactionPresenter: SActionPresenter

    companion object {
        val TAG : String = "SActionActivity"
        fun gotoSelectActionActivityForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, SActionActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        sactionPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.select_action)

        sactionAdapter.setClickListener(this)

        onClick()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        sactionPresenter.init()
        sactionPresenter.getDataFromServer()

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sactionAdapter
        }
    }

    override fun layoutId(): Int = R.layout.activity_select_action

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDeviceData(myDevices: List<DeviceBean>) {
        if (sactionAdapter != null) {
            sactionAdapter?.apply {
                setData(myDevices)
                notifyDataSetChanged()
            }
        }
    }

    override fun onClickDevice(device: DeviceBean) {
        val i = Intent(this, SFunctionActivity::class.java)
        i.putExtra("devId", device.devId)
        i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, true)
    }

    override fun snackBarLayoutId(): ViewGroup = select_action_id

    override fun onDestroy() {
        super.onDestroy()
        sactionPresenter.onDestroy()
        sactionPresenter.detachView()
    }

    fun onClick(){
        ll_timelapse.setOnClickListener {

        }
    }

}