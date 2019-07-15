package com.cherry.home.ui.scenario.automation.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.action.AActionActivity
import com.cherry.home.ui.scenario.automation.condition.ACondActivity
import com.cherry.home.ui.scenario.autosettings.timeperiod.TimePeriodActivity
import com.cherry.home.ui.scenario.model.DeviceScene
import com.cherry.home.ui.scenario.scene.settings.SSettingAdapter
import com.cherry.home.ui.scenario.scenebg.SceneBackgroundActivity
import com.cherry.home.ui.scenario.scene.settings.SSettingsPresenter
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.SwipeToDeleteCallback
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import kotlinx.android.synthetic.main.activity_auto_setting.*
import kotlinx.android.synthetic.main.bottom_sheet_condition.view.*
import kotlinx.android.synthetic.main.login_dialog.view.*
import javax.inject.Inject

class AutoActivity : BaseActivity(), AutoView, AutoAdapter.Clicklistener{


    @Inject lateinit var autoPresenter: AutoPresenter
    @Inject lateinit var actionAdapter : SSettingAdapter
    @Inject lateinit var autoAdapter: AutoAdapter

    private lateinit var sceneBean: SceneBean
    private var sceneName : String? = null
    private var requestCode: Int = 0

    companion object {
        val TAG : String = "AutoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        autoPresenter.attachView(this)
        initToolbar()
        setToolbarTitle(R.string.smart_settings)

        if(intent.extras != null){
            sceneBean = intent.getSerializableExtra("scene") as SceneBean
            requestCode = intent.getIntExtra("requestCode", 0)
            autoPresenter.modifyAction(sceneBean)
        }
        else
        {
            autoPresenter.getAutoCover()
        }

        recyclerView_auto_setting.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = autoAdapter
        }

        recyclerView_actions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = actionAdapter
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView_auto_setting.adapter as AutoAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView_auto_setting)


        initView()
        autoPresenter.getAutoCover()
        onclick()

        autoAdapter.setClicklistener(this)
    }

    override fun onResume() {
        super.onResume()

        if (requestCode == Constant.REQUEST_MODIFY_AUTO) {
            tv_delete_scene.visible()
        } else {
            tv_delete_scene.gone()
        }

        if (autoAdapter.itemCount != 0){
            recyclerView_auto_setting.visible()
            btn_add_conditions.gone()
            view_linesepa_condition.visible()
        } else {
            recyclerView_auto_setting.gone()
            btn_add_conditions.visible()
            view_linesepa_condition.gone()
        }

        if (actionAdapter.itemCount != 0){
            recyclerView_actions.visible()
            btn_add_actions.gone()
            view_linesepa_action.visible()
        } else {
            recyclerView_actions.gone()
            btn_add_actions.visible()
            view_linesepa_action.gone()
        }

    }

    override fun layoutId(): Int = R.layout.activity_auto_setting

    override fun snackBarLayoutId(): ViewGroup = scene_auto_setting_id

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_smart_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId) {
        R.id.save_smart_settings -> {
            save()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun showError(error_msg: String) {
        showSnack(snackBarLayoutId(), error_msg, Snackbar.LENGTH_SHORT)
    }

    override fun errToastCondition() {
        Toast.makeText(this, R.string.set_condition_action, Toast.LENGTH_SHORT).show()
    }

    override fun setImage(image: String) {
        val radius = 10
        Picasso.with(this).load(image).into(iv_cover_auto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "requestcode $requestCode  resultcode $resultCode data $data")
        autoPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun setAutomationTask(auto: MutableList<DeviceScene>) {
        autoAdapter?.apply {
            setAuto(auto)
            notifyDataSetChanged()
        }
    }

    override fun setActionTask(scene: MutableList<DeviceScene>) {
        Log.d(TAG, "setActionTask : ${scene.size}")
        actionAdapter?.apply {
            setTask(scene)
            notifyDataSetChanged()
        }
    }

    override fun returnSceneFragment(status: String) {
        val intent = Intent()
        intent.putExtra("status", status)
        setResult(Activity.RESULT_OK, intent)
//        finish()
        finishActivity()
    }

    override fun setScenarioName(name: String) {
        sceneName = name
        tv_scene_name_auto.text = sceneName
    }

    override fun setConditionType(type: Int) {
        when(type){
            SceneBean.MATCH_TYPE_AND ->{
                lbl_condition_met.text = AutoPresenter.ALL_CONDITION
            }
            SceneBean.MATCH_TYPE_OR ->{
                lbl_condition_met.text = AutoPresenter.ANY_CONDITION
            }
        }
    }

    override fun onBackPressed() {
        saveChangeDialog()
    }

    override fun clickCondition(sceneCondition: DeviceScene) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onDestroy() {
        super.onDestroy()
        autoPresenter.onDestroy()
        autoPresenter.detachView()
    }

    private fun showEditName(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.login_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Edit Name")
        val  mAlertDialog = mBuilder.show()
        mDialogView.dialogLoginBtn.setOnClickListener {
            mAlertDialog.dismiss()

            sceneName = mDialogView.dialogNameEt.text.toString()
            tv_scene_name_auto.text = sceneName
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun saveChangeDialog(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.savechange_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            mAlertDialog.dismiss()
            save()
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
            finish()
        }
    }


    private fun initView(){
        val curveRadius = 20F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            iv_cover_auto.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
                }
            }

            iv_cover_auto.clipToOutline = true
        }
    }

    private fun save(){
        if(sceneName.isNullOrBlank()){
            showEditName()
            if(!sceneName.isNullOrBlank() && autoAdapter.itemCount != 0 && actionAdapter.itemCount != 0 ){
                val condition = lbl_condition_met.text.toString()
                autoPresenter.saveAuto(sceneName!!, condition)
            }
        }else {
            val condition = lbl_condition_met.text.toString()
            if (autoPresenter.getSceneBeanActionSize() != 0){
                autoPresenter.saveModifyAuto(sceneBean, sceneName!!, condition)
            }else{
                Log.d(TAG, "save111 saveAuto 2 ${autoPresenter.getSceneBeanActionSize()}  action ${autoPresenter.getActionListSize()}  " +
                        "list ${autoPresenter.getAutoListSize()} autoadapter ${autoAdapter.itemCount}  actionadapter ${actionAdapter.itemCount}")
                if (autoAdapter.itemCount == 0 || actionAdapter.itemCount == 0){
                    errToastCondition()
                }
                else {
                    autoPresenter.saveAuto(sceneName!!, condition)
                }
            }
        }
    }

    private fun bottomSheet(){
        val dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_condition, null)

        bottomSheet.condition_cancel.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(bottomSheet)
        dialog.show()

        bottomSheet.all_condition.setOnClickListener {
            lbl_condition_met.setText(R.string.all_conditions)
            dialog.dismiss()
        }

        bottomSheet.any_condition.setOnClickListener {
            lbl_condition_met.setText(R.string.any_conditions)
            dialog.dismiss()
        }
    }

    private fun onclick(){
        iv_cover_auto.setOnClickListener {
            SceneBackgroundActivity.gotoSceneBackgroundForResult(this, SSettingsPresenter.REQUEST_SCENE_BG)
        }

        btn_add_conditions.setOnClickListener {
            ACondActivity.gotoAutoCondActivityForResult(this, Constant.REQUEST_SCENE_AUTO)
        }

        btn_add_actions.setOnClickListener {
            AActionActivity.gotoAutoActionActivityForResult(this, Constant.REQUEST_SCENE_TASK)
        }

        btn_plus_condition.setOnClickListener {
            ACondActivity.gotoAutoCondActivityForResult(this, Constant.REQUEST_SCENE_AUTO)
        }

        btn_plus_actions.setOnClickListener {
            AActionActivity.gotoAutoActionActivityForResult(this, Constant.REQUEST_SCENE_TASK)
        }

        lbl_condition_met.setOnClickListener {
            bottomSheet()
        }

        iv_dropdown.setOnClickListener {
            bottomSheet()
        }

        tv_scene_name_auto.setOnClickListener {
            showEditName()
        }

        rl_editname_auto3.setOnClickListener {
            val i = Intent(this, TimePeriodActivity ::class.java)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, false)
        }

    }
}