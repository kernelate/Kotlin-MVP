package com.cherry.home.ui.homemanagement

import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import javax.inject.Inject


@ConfigPersistent
class HomeManagementPresenter @Inject constructor() : BasePresenter<HomeManagementView>(){


    fun queryHome(){
        checkViewAttached()
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback{
            override fun onSuccess(homeBeanList: MutableList<HomeBean>) {
                if(homeBeanList != null){
                    mvpView?.getDataFromServer(homeBeanList)
                }
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }

        })
    }

}