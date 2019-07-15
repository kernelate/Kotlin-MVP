package com.cherry.home.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.google.android.gms.maps.model.LatLng
import com.tuya.smart.android.base.utils.PreferencesUtil
import com.tuya.smart.android.user.api.IBooleanCallback
import com.tuya.smart.android.user.api.ILogoutCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
import com.tuya.smart.sdk.api.IResultCallback
import java.io.File
import javax.inject.Inject

@ConfigPersistent
class ProfilePresenter @Inject constructor() : BasePresenter<ProfileView>() {

    private val TAG : String = "ProfilePresenter"

    private lateinit var homeBean: HomeBean
    private lateinit var iTuyaUser : User

    private var homeId : Long = 0

    companion object {
        internal val REQUEST_LOCATION = 1
        internal val REQUEST_TIMEZONE = 2
        internal val REQUEST_IMAGE : Int = 3
        internal val REQUEST_EXTERNAL_STORAGE = 1
        internal val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    fun init(){
        checkViewAttached()
        homeId = PreferencesUtil.getLong("homeId", Constant.HOME_ID)
        iTuyaUser = TuyaHomeSdk.getUserInstance().user!!

        getHomeDetail()
    }

    fun updateProfileName(name : String){
        iTuyaUser.nickName = name
        TuyaHomeSdk.getUserInstance().saveUser(iTuyaUser)
        mvpView?.setUserNickname(iTuyaUser.nickName)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_LOCATION ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    val loc = data.getStringExtra("home")
                    val latlng = data.getParcelableExtra("latLng") as LatLng
                    updateHomeLocation(latlng.longitude, latlng.latitude, loc)
                }
            }
            REQUEST_TIMEZONE ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    val timeZone = data.getStringExtra("timeZone")
                    setTimeZone(timeZone)
                }
            }
            REQUEST_IMAGE ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    val URI  = data.data
                    val path : File = mvpView?.getRealPathFromURI(URI)!!
                    uploadPhoto(path)
                }
            }
        }
    }

    fun logOut(){
        TuyaHomeSdk.getUserInstance().logout(object : ILogoutCallback {
            override fun onSuccess() {
                mvpView?.reLogin()
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun getUserInfo(){

        mvpView?.apply {
            if(!iTuyaUser.headPic.isNullOrBlank()){
                setUserProfile(iTuyaUser.headPic)
            }
            if(!iTuyaUser.nickName.isNullOrBlank()){
                setUserNickname(iTuyaUser.nickName)
            }
            if(!iTuyaUser.timezoneId.isNullOrBlank()){
                setTimeZone(iTuyaUser.timezoneId)
            }
            setUserEmail(iTuyaUser.username)
            setLocation(homeBean.geoName)

        }
    }

    private fun getHomeDetail(){
        TuyaHomeSdk.newHomeInstance(homeId).getHomeLocalCache(object : ITuyaHomeResultCallback {
            override fun onSuccess(home: HomeBean) {
                if(home != null){
                    homeBean = home
                    getUserInfo()
                }
            }
            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun updateHomeLocation(lon : Double, lat : Double, geoName : String){
        TuyaHomeSdk.newHomeInstance(homeId).updateHome(homeBean.name, lon, lat, geoName, object : IResultCallback {
            override fun onSuccess() {
                mvpView?.setLocation(geoName)
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }

        })
    }

    private fun setTimeZone(timeZone : String){
        iTuyaUser.timezoneId = timeZone
        TuyaHomeSdk.getUserInstance().saveUser(iTuyaUser)
        mvpView?.setTimeZone(timeZone)
    }

    private fun uploadPhoto(file: File) {
        mvpView?.showProgress()
        TuyaHomeSdk.getUserInstance().uploadUserAvatar(file, object : IBooleanCallback {
            override fun onSuccess() {
                mvpView?.setUserProfile(iTuyaUser.headPic)
                mvpView?.hideProgress()
                init()
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }
}