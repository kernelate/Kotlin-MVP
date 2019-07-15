package com.cherry.home.ui.scenario.automation.control

import android.app.Activity
import android.content.Intent
import android.databinding.adapters.NumberPickerBindingAdapter.setValue
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.function.AFunctionPresenter
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.home.sdk.bean.scene.SceneTask
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import kotlinx.android.synthetic.main.activity_auto_control.*
import kotlinx.android.synthetic.main.toolbar_top.*
import javax.inject.Inject

class AControlActivity : BaseActivity(), AControlView, AControlBoolAdapter.ClickListener {


    private val TAG : String = "AControlActivity"

    @Inject lateinit var acontrolPresenter: AControlPresenter

    @Inject lateinit var acontrolBoolAdapter: AControlBoolAdapter

    @Inject lateinit var aControlValueAdapter: AControlValueAdapter

    private var countdown : Int = 0

    companion object {
        fun gotoAutoControlActivityForResult(mContext: Activity, taskListBean: TaskListBean, devId: String, requestCode: Int) {
            val intent = Intent(mContext, AControlActivity :: class.java)
            intent.putExtra("task", taskListBean)
            intent.putExtra("devId", devId)
            intent.putExtra("requestCode", requestCode)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        acontrolPresenter.attachView(this)

        var devId= intent.getStringExtra(AFunctionPresenter.DEV_ID)
        var taskListBean = intent.getSerializableExtra("task") as TaskListBean
        var requestCode = intent.getIntExtra("requestCode", -1)

        initToolbar()
        setToolbarTitle(R.string.select_action)

        acontrolPresenter.init(devId,taskListBean, requestCode)

        recyclerView_auto_control?.apply {
            layoutManager = LinearLayoutManager(context)
            if (acontrolPresenter.getTaskType() == "bool" || acontrolPresenter.getTaskType() =="enum"){
                adapter = acontrolBoolAdapter
            }
            else if (acontrolPresenter.getTaskType() == "value"){
                adapter = aControlValueAdapter
            }
        }
        acontrolBoolAdapter.setClicklistener(this)

//        main_toolbar.setNavigationOnClickListener {
//            acontrolPresenter.setValue(countdown)
//        }
    }

    override fun layoutId(): Int = R.layout.activity_auto_control

    override fun snackBarLayoutId(): ViewGroup = rl_child_auto_control

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
        acontrolPresenter.getTask()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_next, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.next_button -> {
            Log.d(TAG, " next upperright")
            acontrolPresenter.setValue(countdown)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
//161,397
    override fun onClickDevice(device: String) {
        acontrolPresenter.setControls(device)
    }

    override fun updateControl(task: MutableList<String>, requestCode: Int) {
        Log.d(TAG, "requestCode $requestCode")
        when(acontrolPresenter.getTaskType()){
            "bool", "enum" -> {
                if (acontrolBoolAdapter != null) {
                    recyclerView_auto_control.visible()
//                    numberPicker.gone()
                    acontrolBoolAdapter?.apply {
                        setValue(task)
                        notifyDataSetChanged()
                    }
                }
            }

            "value" -> {
//                when(requestCode){
//                    Constant.REQUEST_SCENE_TASK ->{
                        val values = arrayOf("Smaller than", "Equals", "Greater than")
                        numberPicker2.displayedValues = values
                        numberPicker2.minValue = 0
                        numberPicker2.maxValue = values.size-1
                        numberPicker2.wrapSelectorWheel = true

                        recyclerView_auto_control.gone()
                        numberPicker.visible()
                        numberPicker.minValue = 0
                        numberPicker.maxValue = acontrolPresenter.getMaxTaskList()
                        numberPicker.setFormatter { i -> String.format("%ds", i) }
                        numberPicker.setOnValueChangedListener { numberPicker, a, b ->
                            countdown = numberPicker.value
                        }
//                    }
//                }
            }
        }
    }

    override fun setAction(task: SceneTask, taskListBean: TaskListBean, value : String) {
        val result = Intent()
        result.putExtra("task", task)
        result.putExtra("tasklist", taskListBean)
        result.putExtra("value", value)
        setResult(Activity.RESULT_OK, result)
        finishActivity()
    }

    override fun setCondition(condition: SceneCondition, taskListBean: TaskListBean, value : String) {
        val result = Intent()
        result.putExtra("condition", condition)
        result.putExtra("tasklist", taskListBean)
        result.putExtra("value", value)
        setResult(Activity.RESULT_OK, result)
        finishActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
//        acontrolPresenter.detachView()
    }
}