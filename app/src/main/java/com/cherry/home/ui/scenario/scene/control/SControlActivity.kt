package com.cherry.home.ui.scenario.scene.control

import android.app.Activity
import android.content.Intent
import android.databinding.adapters.NumberPickerBindingAdapter.setValue
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.scene.function.SFunctionPresenter
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import kotlinx.android.synthetic.main.activity_scenario_control.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.toolbar_top.*


class SControlActivity : BaseActivity(), SControlView, SBoolAdapter.ClickListener {

    private val TAG : String = "SControlActivity"

    @Inject lateinit var scontrolPresenter: SControlPresenter

    @Inject lateinit var sboolAdapter: SBoolAdapter

    @Inject lateinit var senumAdapter: SEnumAdapter

    @Inject lateinit var valueAdapter: ValueAdapter

    private var countdown : Int = 0

    companion object {
        fun gotoScenarioControlActivityForResult(mContext: Activity, taskListBean: TaskListBean, devId: String, requestCode: Int) {
            val intent = Intent(mContext, SControlActivity::class.java)
            intent.putExtra("task", taskListBean)
            intent.putExtra("devId", devId)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        scontrolPresenter.attachView(this)

        var devId= intent.getStringExtra(SFunctionPresenter.DEV_ID)
        var taskListBean = intent.getSerializableExtra("task") as TaskListBean

        initToolbar()
        setToolbarTitle(R.string.select_function)

        scontrolPresenter.init(devId, taskListBean)

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            if (scontrolPresenter.getTaskType() == "bool" || scontrolPresenter.getTaskType() == "enum"){
                adapter = sboolAdapter
            }
            else if (scontrolPresenter.getTaskType() == "value"){
                adapter = valueAdapter
            }
        }

        sboolAdapter.setClicklistener(this)

        main_toolbar.setNavigationOnClickListener {
            Log.d(TAG, "countdown $countdown")
            scontrolPresenter.setValue(countdown)
        }

    }

    override fun onResume() {
        super.onResume()
        scontrolPresenter.getTask()
    }

    override fun layoutId(): Int = R.layout.activity_scenario_control

    override fun snackBarLayoutId(): ViewGroup = rl_child_scene_control

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTask(task: SceneTask, taskListBean: TaskListBean, value : String) {
        val result = Intent()
        result.putExtra("task", task)
        result.putExtra("tasklist", taskListBean)
        result.putExtra("value", value)
        setResult(Activity.RESULT_OK, result)
        finishActivity()
//        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_FORWARD,true)
    }

    override fun onClickDevice(control: String) {
        scontrolPresenter.setControls(control)
    }

    override fun updateControl(task : MutableList<String>) {
        when(scontrolPresenter.getTaskType()){
            "bool", "enum" ->{
                if (sboolAdapter != null) {
                    recyclerView.visible()
                    numberPicker.gone()
                    sboolAdapter?.apply {
                        setValue(task)
                        notifyDataSetChanged()
                    }
                }
            }
            "value" ->{
                // on numberpicker
                recyclerView.gone()
                numberPicker.visible()
                numberPicker.minValue = 0
                numberPicker.maxValue = scontrolPresenter.getMaxTaskValue()
                numberPicker.setFormatter { i -> String.format("%ds", i) }
                numberPicker.setOnValueChangedListener { numberPicker,  a, b ->
                    Toast.makeText(this,
                            "selected number "+numberPicker.value, Toast.LENGTH_SHORT)
                    countdown = numberPicker.value
                }
//                numberPicker.displayedValues = SControlPresenter.getDisplayValue()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}