package com.cherry.home.ui.scenario.scene.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Outline
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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
import com.cherry.home.ui.scenario.model.DeviceScene
import com.cherry.home.ui.scenario.scenebg.SceneBackgroundActivity
import com.cherry.home.ui.scenario.scene.action.SActionActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.SwipeToDeleteCallback
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import kotlinx.android.synthetic.main.activity_scene_setting.*
import kotlinx.android.synthetic.main.login_dialog.view.*
import javax.inject.Inject

class SSettingsActivity : BaseActivity(), SSettingsView, SSettingAdapter.Clicklistener {

    @Inject lateinit var ssettingPresenter: SSettingsPresenter

    @Inject lateinit var ssettingAdapter: SSettingAdapter

    private lateinit var sceneBean: SceneBean
    private var sceneName : String? = null
    private var requestCode: Int = 0

    companion object {
        val TAG : String = "SSettingsActivity"
        fun gotoSceneSettingsActivityForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, SSettingsActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

    override fun layoutId(): Int = R.layout.activity_scene_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        ssettingPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.smart_settings)


        if(intent.extras != null){
            sceneBean = intent.getSerializableExtra("scene") as SceneBean
            requestCode = intent.getIntExtra("requestCode", 0)
            ssettingPresenter.modifyScene(sceneBean)
        } else {
            ssettingPresenter.getSceneCover()
        }

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ssettingAdapter
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as SSettingAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        initView()
        onClick()

        ssettingAdapter.setClicklistener(this)

    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        if(outState != null){
//            outState.putSerializable("scene", sceneBean)
//        }
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        sceneBean = savedInstanceState?.getSerializable("scene") as SceneBean
//    }

    override fun onResume() {
        super.onResume()

        if (requestCode == Constant.REQUEST_MODIFY_SCENETASK) {
            tv_delete_scene.visible()
        } else {
            tv_delete_scene.gone()
        }

        if (ssettingAdapter.itemCount != 0){
            recyclerView.visible()
            btn_add_action.gone()
            view_linesepa.visible()
        }
        else {
            recyclerView.gone()
            btn_add_action.visible()
            view_linesepa.gone()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_smart_settings, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.save_smart_settings -> {
            Log.d(TAG, " savebtn upperright")
            save()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun errToast() {
        Toast.makeText(this, R.string.set_actions, Toast.LENGTH_SHORT).show()
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error_msg: String) {
        showSnack(snackBarLayoutId(), R.string.set_actions, Snackbar.LENGTH_LONG, null)
    }

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun snackBarLayoutId(): ViewGroup = scene_setting_id

    override fun setImage(image: String) {
        val radius = 10
        Picasso.with(this).load(image).into(iv_cover)
    }

    override fun setSceneTask(task: MutableList<DeviceScene>) {
        ssettingAdapter?.apply {
            setTask(task)
            notifyDataSetChanged()
        }
    }

    override fun setScenarioName(name: String) {
        sceneName = name
        tv_scene_name.text = sceneName
    }

    override fun gotoSceneFragment() {
        val intent = Intent()
        setResult(Activity.RESULT_OK)
        finishActivity()
    }

    override fun returnSceneFragment(status : String) {
        Log.d(TAG, "savebtn returnscene")
        val intent = Intent()
        intent.putExtra("status", status)
        setResult(Activity.RESULT_OK, intent)
        finishActivity()
    }

    override fun clickAction(sceneTask: DeviceScene) {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ssettingPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        saveChangeDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        ssettingPresenter.onDestroy()
        ssettingPresenter.detachView()
    }

    private fun showEditName(){
        Log.d(TAG, "savebtn showEditName")
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.login_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Edit Name")
        val  mAlertDialog = mBuilder.show()
        mDialogView.dialogLoginBtn.setOnClickListener {
            mAlertDialog.dismiss()
            if(!mDialogView.dialogNameEt.text.isNullOrBlank()){

                sceneName = mDialogView.dialogNameEt.text.toString()
                tv_scene_name.text = sceneName
            }
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }



    private fun onClick(){
        btn_add_action.setOnClickListener {
            SActionActivity.gotoSelectActionActivityForResult(this, Constant.REQUEST_SCENE_TASK)
        }

        scene_bg.setOnClickListener {
            SceneBackgroundActivity.gotoSceneBackgroundForResult(this, SSettingsPresenter.REQUEST_SCENE_BG)
        }

        btn_add_scene.setOnClickListener {
            //            val i = Intent(this, SActionActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
//            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
            SActionActivity.gotoSelectActionActivityForResult(this, Constant.REQUEST_SCENE_TASK)
        }

        tv_scene_name.setOnClickListener {
            showEditName()
        }

        tv_delete_scene.setOnClickListener {
            ssettingPresenter.deleteScene(sceneBean)
        }


    }


    private fun initView(){
        val curveRadius = 20F
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            iv_cover.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
                }
            }
            iv_cover.clipToOutline = true
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

    private fun save(){
        Log.d(TAG, " savebtn loob ")
        if(sceneName.isNullOrBlank()){
            showEditName()
            if(!sceneName.isNullOrBlank() && ssettingAdapter.itemCount != 0){
                Log.d(TAG, " ssettingadapter  ${ssettingAdapter.itemCount} ")
                ssettingPresenter.saveScene(sceneName!!)
            }
        } else {
            if(ssettingPresenter.getSceneBeanActionSize() != 0){
                Log.d(TAG, " settingpresenter saveModifyScene")
                ssettingPresenter.saveModifyScene(sceneBean, sceneName!!)
            } else {
                Log.d(TAG, " settingpresenter savescene")
                ssettingPresenter.saveScene(sceneName!!)
            }
        }
    }

}