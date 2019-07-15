package com.cherry.home.ui.scenario.autosettings.timeperiod

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.google.android.gms.maps.model.LatLng
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import com.tuya.smart.home.sdk.bean.scene.condition.rule.Rule
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import javax.inject.Inject


@ConfigPersistent
class TimePeriodPresenter @Inject constructor() : BasePresenter<TimePeriodView>(){

    private val TAG : String = "TimePeriodPresenter"
    private lateinit var homeBean: HomeBean

    private var homeId : Long = 0
     var latLng: LatLng? = null
     var homeLoc : String? = null

    fun init() {
        checkViewAttached()
        homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        getHomeDetail()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            Constant.REQUEST_LOCATION ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    Log.d(TAG, "loc11 request loc")
//                    val loc = data.getStringExtra("home")
//                    val latlng = data.getParcelableExtra("latLng") as LatLng
                    homeLoc = data.getStringExtra("home")
                    latLng = data.getParcelableExtra("latLng") as LatLng
//                    updateHomeLocation(latLng!!.longitude, latLng!!.latitude, homeLoc!!)
                    mvpView?.getCityName(latLng!!.latitude, latLng!!.longitude)
                }
            }
        }
    }

    private fun getHomeDetail(){
        TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(object : ITuyaHomeResultCallback {
            override fun onSuccess(home: HomeBean) {
                if(home != null){
                    homeBean = home
                    mvpView?.setLocation(home.geoName)

                    Log.d(TAG, "loc11 geo ${home.geoName}")
                }
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun updateHomeLocation(lon : Double, lat : Double, geoName : String) {
        Log.d(TAG, "loc11 geo ${geoName}")

        TuyaHomeSdk.newHomeInstance(homeId).updateHome(homeBean.name, lon, lat, geoName, object : IResultCallback {
            override fun onSuccess() {
                mvpView?.setLocation(geoName)
                Log.d(TAG, "loc11 geo ${geoName} ")
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    fun createTimerCondition(display : String, name : String, type : String, rule : Rule) {

        SceneCondition.createTimerCondition(display, name, type, rule)

    }


}