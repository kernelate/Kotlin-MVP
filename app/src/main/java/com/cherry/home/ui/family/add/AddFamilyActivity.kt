package com.cherry.home.ui.family.add

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.family.model.AddFamilyModel
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.ProgressUtil
import com.cherry.home.util.map.MapsActivity
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import kotlinx.android.synthetic.main.activity_add_family.*
import javax.inject.Inject

class AddFamilyActivity : BaseActivity(), AddFamilyView, TextWatcher {

    private val TAG : String = "AddFamilyActivity"

    @Inject lateinit var addFamilyPresenter: AddFamilyPresenter

    private var isEmptyFamily: Boolean = false

    companion object {
        val KEY_EMPTY_FAMILY = "empty_family_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        addFamilyPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.add_family)

        isEmptyFamily = intent.getBooleanExtra(KEY_EMPTY_FAMILY, false)
        et_name.addTextChangedListener(this)
        onClick()
    }

    override fun layoutId(): Int = R.layout.activity_add_family


    override fun saveFamily() {
        finishActivity()
        if(isEmptyFamily){
            Constant.finishActivity()
            ActivityUtils.gotoMainActivity(this)
        }
    }

    override fun showFailed() {
    }

    override fun showLoading() {
        ProgressUtil.showLoading(this, "Loading...")
    }

    override fun hideLoading() {
        ProgressUtil.hideLoading()
    }

    override fun createHome(homeName: String, longitude: Double, latitude: Double, geoName: String?, roomList: List<String>, callback: ITuyaHomeResultCallback) {
        AddFamilyModel.createHome(this, homeName, longitude, latitude, geoName,  roomList, callback)
    }

    override fun snackBarLayoutId(): ViewGroup = add_family_id


    override fun setLocation(location: String) {
        tvLocation.text = location
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        addFamilyPresenter.onActivityResult(requestCode, resultCode,data)
    }

    override fun afterTextChanged(p0: Editable?) {
        if (et_name.text.isNullOrBlank() ){
            disable()
        }else{
            enable()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        addFamilyPresenter.detachView()
    }

    private fun disable(){
        if (btn_create.isEnabled){
            btn_create.isEnabled = false
            btn_create.setBackgroundResource(R.drawable.gray_button_bg)
        }
    }

    private fun enable(){
        if (!btn_create.isEnabled){
            btn_create.isEnabled = true
            btn_create.setBackgroundResource(R.drawable.gradient_red_square)
        }
    }

    private fun onClick(){
        btn_create.setOnClickListener {
            val name = et_name.text.toString()
            val loc = tvLocation.text.toString()
            addFamilyPresenter.addHome(name, loc)
        }

        familyloc_linear.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, addFamilyPresenter.REQUEST_LOCATION, 0, false)
        }
    }
}