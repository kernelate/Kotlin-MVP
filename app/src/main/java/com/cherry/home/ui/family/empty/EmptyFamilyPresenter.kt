package com.cherry.home.ui.family.empty

import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.user.api.ILogoutCallback
import com.tuya.smart.home.sdk.TuyaHomeSdk
import javax.inject.Inject

@ConfigPersistent
class EmptyFamilyPresenter @Inject constructor(): BasePresenter<EmptyFamilyView>() {

    fun logout(){
        checkViewAttached()
        TuyaHomeSdk.getUserInstance().logout(object : ILogoutCallback {
            override fun onSuccess() {
                mvpView?.apply {
                    hideLoading()
                    reLogin()
                }
            }

            override fun onError(errorCode: String, errorMsg: String) {
                mvpView?.apply {
                    hideLoading()
                    showError(errorMsg)
                }
            }
        })

    }
}