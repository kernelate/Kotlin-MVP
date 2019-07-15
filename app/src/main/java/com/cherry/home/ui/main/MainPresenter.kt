package com.cherry.home.ui.main

import android.util.Log
import com.cherry.home.app.Constant
import com.cherry.home.data.DataManager
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.cherry.home.util.CollectionUtils
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import javax.inject.Inject

@ConfigPersistent
class MainPresenter @Inject constructor(private val dataManager: DataManager, private val preferenceHelper: AppPreferenceHelper) : BasePresenter<MainMvpView>() {


    fun getFamily(){
        checkViewAttached()
        mvpView?.apply {
            queryHomeList(object  : ITuyaGetHomeListCallback {
                override fun onSuccess(list: MutableList<HomeBean>) {
                    if(CollectionUtils.isEmpty(list)) {
                        Constant.finishActivity()
                        gotoFamilyActivity()
                    } else {
                        setFamilyName(list[0].name)
                    }

                }

                override fun onError(error: String, error_msg: String) {
                    showError(error, error_msg)
                }

            })
        }
    }

    private fun getFamilyName(){
        mvpView?.apply {
            val currentHome = getCurrentHome()
            if(null != currentHome){
                setFamilyName(currentHome.name)
            }
        }
    }

    fun getUsername(){
        mvpView?.setUsername(TuyaHomeSdk.getUserInstance().user!!.username)
    }

    fun getProfilepic(){
        if(!TuyaHomeSdk.getUserInstance().user?.headPic.isNullOrBlank())
            mvpView?.setProfile(TuyaHomeSdk.getUserInstance().user!!.headPic)
    }

}