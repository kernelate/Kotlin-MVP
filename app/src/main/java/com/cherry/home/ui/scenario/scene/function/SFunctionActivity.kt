package com.cherry.home.ui.scenario.scene.function

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.scene.control.SControlActivity
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import kotlinx.android.synthetic.main.activity_select_function.*
import javax.inject.Inject

class SFunctionActivity : BaseActivity(), SFunctionView, SFunctionAdapter.ClickListener {

    @Inject lateinit var sfunctionPresenter: SFunctionPresenter

    @Inject lateinit var sfunctionAdapter: SFunctionAdapter

    private lateinit var devId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        sfunctionPresenter.attachView(this)

        if(savedInstanceState == null){
            devId = intent.getStringExtra("devId")
        }

        initToolbar()
        setToolbarTitle(R.string.select_function)
        sfunctionAdapter.setClicklistener(this)
    }

    override fun onResume() {
        super.onResume()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sfunctionAdapter
        }
        sfunctionPresenter.getFunction(devId)
    }

    override fun layoutId(): Int = R.layout.activity_select_function

    override fun snackBarLayoutId(): ViewGroup = rl_child_scene

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDevice(function: List<TaskListBean>) {
        if (sfunctionAdapter != null) {
            sfunctionAdapter?.apply {
                setData(function)
                notifyDataSetChanged()
            }
        }
    }

    override fun onClickDevice(device: TaskListBean) {
        SControlActivity.gotoScenarioControlActivityForResult(this, device, devId, Constant.REQUEST_SCENE_TASK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constant.REQUEST_SCENE_TASK){
            setResult(resultCode, data)
            finishActivity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sfunctionPresenter.onDestroy()
        sfunctionPresenter.detachView()
    }
}