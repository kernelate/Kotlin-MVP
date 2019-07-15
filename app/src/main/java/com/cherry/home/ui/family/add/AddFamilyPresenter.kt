package com.cherry.home.ui.family.add

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.google.android.gms.maps.model.LatLng
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import javax.inject.Inject

@ConfigPersistent
class AddFamilyPresenter @Inject constructor() : BasePresenter<AddFamilyView>() {

    internal val REQUEST_LOCATION = 1

    private val TAG : String = "AddFamilyPresenter"

    private lateinit var latLng: LatLng

    fun addHome(name: String, location : String) {
        checkViewAttached()
        if(!location.isNullOrBlank()){
            mvpView?.createHome(name, latLng.longitude, latLng.latitude, location, Constant.roomlist, object : ITuyaHomeResultCallback{
                override fun onSuccess(homeBean: HomeBean?) {
                    mvpView?.apply {
                        hideLoading()
                        saveFamily()
                    }
                }

                override fun onError(code: String?, msg: String?) {
                    mvpView?.apply {
                        hideLoading()
                        showFailed()
                    }
                }

            })
        } else {
            mvpView?.createHome(name, 0.0, 0.0, null, Constant.roomlist, object : ITuyaHomeResultCallback{
                override fun onSuccess(homeBean: HomeBean?) {
                    mvpView?.apply {
                        hideLoading()
                        saveFamily()
                    }
                }

                override fun onError(code: String?, msg: String?) {
                    mvpView?.apply {
                        hideLoading()
                        showFailed()
                    }
                }

            })
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_LOCATION ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    val loc = data.getStringExtra("home")
                    latLng = data.getParcelableExtra("latLng") as LatLng
                    mvpView?.setLocation(loc)
                }
            }
        }
    }

}