package com.cherry.home.ui.family.settings

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.google.android.gms.maps.model.LatLng
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.bean.MemberBean
import com.tuya.smart.home.sdk.callback.ITuyaGetMemberListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import javax.inject.Inject

@ConfigPersistent
class FamilySettingsPresenter @Inject constructor() : BasePresenter<FamilySettingsView>() {

    private lateinit var homeBean: HomeBean

    private val TAG : String = "FamilySettingsPresenter"
    private var latLng: LatLng? = null
    private var homeLoc : String? = null

    fun init(homeId: Long){
        checkViewAttached()
        getHomeDetail(homeId)
        queryMemberList(homeId)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            Constant.REQUEST_LOCATION ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    homeLoc = data.getStringExtra("home")
                    latLng = data.getParcelableExtra("latLng") as LatLng
                    updateHomeLocation(latLng!!.longitude, latLng!!.latitude, homeLoc!!)
                }
            }
        }
    }

    private fun getHomeDetail(homeId : Long){
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback{
            override fun onSuccess(home: HomeBean) {
                if(home != null){
                    homeBean = home
                    mvpView?.apply {
                        setFamilyName(home.name)
                        setLocation(home.geoName)
                    }
                }
            }
            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun queryMemberList(homeId : Long){
        TuyaHomeSdk.getMemberInstance().queryMemberList(homeId, object : ITuyaGetMemberListCallback{
            override fun onSuccess(memberBeanList: MutableList<MemberBean>) {
                memberBeanList.forEach {
                    Log.d(TAG, "member  account homeId ${it.homeId} memberid ${it.memberId} username ${it.account} isadmin ${it.isAdmin}")
                }
                mvpView?.getMember(memberBeanList)
            }
            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun updateHomeLocation(lon : Double, lat : Double, geoName : String){
        TuyaHomeSdk.newHomeInstance(homeBean.homeId).updateHome(homeBean.name, lon, lat, geoName, object : IResultCallback{
            override fun onSuccess() {
                mvpView?.setLocation(geoName)
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }

        })
    }

    fun removeFamily(){
        TuyaHomeSdk.newHomeInstance(homeBean.homeId).dismissHome(object : IResultCallback {
            override fun onSuccess() {
                mvpView?.goToHomeManagement()
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }

        })
    }

    fun updateFamilyName(name : String) {
        var lon = 0.0
        var lat = 0.0
        if(latLng != null){
            lon = latLng!!.longitude
            lat = latLng!!.latitude
        }

        TuyaHomeSdk.newHomeInstance(homeBean.homeId).updateHome(name, lon, lat, homeLoc, object : IResultCallback {
            override fun onSuccess() {

            }

            override fun onError(error: String, msg: String) {

            }

        })
    }

}