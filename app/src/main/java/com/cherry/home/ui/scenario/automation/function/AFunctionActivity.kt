package com.cherry.home.ui.scenario.automation.function

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.control.AControlActivity
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import kotlinx.android.synthetic.main.activity_auto_function.*
import javax.inject.Inject

class AFunctionActivity : BaseActivity(), AFunctionView, AFunctionAdapter.ClickListener {

    private val TAG : String = "AFunctionActivity"

    @Inject lateinit var afunctionPresenter: AFunctionPresenter

    @Inject lateinit var afunctionAdapter: AFunctionAdapter

    private lateinit var devId: String
    private var requestCode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        afunctionPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.select_function)

        if(savedInstanceState == null){
            devId = intent.getStringExtra("devId")
            requestCode = intent.getIntExtra("requestCode", 0)
        }

        recyclerView_auto_function?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = afunctionAdapter
        }

        afunctionAdapter.setClicklistener(this)

        when(requestCode){
            Constant.REQUEST_SCENE_TASK->{
                afunctionPresenter.getActionFunction(devId)
            }
            Constant.REQUEST_SCENE_AUTO->{
                afunctionPresenter.getConditionFunction(devId)
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_auto_function

    override fun snackBarLayoutId(): ViewGroup = rl_auto_function

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

    override fun updateDevice(function: List<TaskListBean>) {
        if (afunctionAdapter != null) {
            afunctionAdapter?.apply {
                setData(function)
                notifyDataSetChanged()
            }
        }
    }

    override fun onClickDevice(device: TaskListBean) {
        AControlActivity.gotoAutoControlActivityForResult(this, device, devId, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(this.requestCode == requestCode){
            setResult(resultCode, data)
            finishActivity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        afunctionPresenter.detachView()
    }
}