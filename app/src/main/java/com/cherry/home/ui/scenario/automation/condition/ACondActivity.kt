package com.cherry.home.ui.scenario.automation.condition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.action.AActionActivity
import com.cherry.home.ui.scenario.automation.conditionaction.AConActionActivity
import com.cherry.home.util.ActivityUtils
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_auto_condition.*
import javax.inject.Inject

class ACondActivity : BaseActivity(), ACondView {

    private val TAG : String = "ACondActivity"

    @Inject lateinit var acondPresenter: ACondPresenter

    companion object {
        fun gotoAutoCondActivityForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, ACondActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        acondPresenter.attachView(this)
        initToolbar()
        setToolbarTitle(R.string.select_condition)

        onClick()
    }


    override fun layoutId(): Int = R.layout.activity_auto_condition

    override fun snackBarLayoutId(): ViewGroup = auto_cond

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
    }

    private fun onClick(){
        lbl_device.setOnClickListener {
            val i = Intent(this, AConActionActivity::class.java)

            i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        acondPresenter.detachView()
    }
}