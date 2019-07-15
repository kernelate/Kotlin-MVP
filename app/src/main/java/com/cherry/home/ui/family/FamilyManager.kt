package com.cherry.home.ui.family

import android.content.Context
import android.util.Log
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.util.CollectionUtils
import com.cherry.home.util.SingletonHolder
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback
//import de.greenrobot.event.EventBus

class FamilyManager private constructor(context: Context){
    val TAG = FamilyManager::class.java.simpleName

    @Volatile
    private var instance: FamilyManager? = null

    private var currentHomeBean : HomeBean? = null

    private var preferenceHelper =  AppPreferenceHelper(context)

    companion object : SingletonHolder<FamilyManager, Context>(::FamilyManager){
    }

    fun setCurrentHome(homeBean: HomeBean?) {
        if (null == homeBean) {
            return
        }
        var isChange = false

        if (null == currentHomeBean) {
            isChange = true
        } else {
            val currentHomeId = currentHomeBean!!.homeId
            val targetHomeId = homeBean.homeId
            if (currentHomeId != targetHomeId) {
                isChange = true
            }
        }
        //更新内存和sp
        currentHomeBean = homeBean
        preferenceHelper.putCurrentHome(currentHomeBean!!)
        if (isChange) {
//            EventBus.getDefault().post(EventCurrentHomeChange(currentHomeBean!!))
        }
    }


    fun getCurrentHome(): HomeBean? {
        if (null == currentHomeBean) {
            setCurrentHome(preferenceHelper.getCurrentHome())
        }
        return currentHomeBean
    }


    fun getCurrentHomeId(): Long {
        val currentHome = getCurrentHome()
        if(null == currentHome){
            return -1
        }
        return currentHome.homeId
    }


    fun getHomeList(callback: ITuyaGetHomeListCallback) {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(object : ITuyaGetHomeListCallback {
            override fun onSuccess(list: List<HomeBean>) {
                if (!CollectionUtils.isEmpty(list) && null == getCurrentHome()) {
                    setCurrentHome(list[0])
                }
                callback.onSuccess(list)
            }

            override fun onError(s: String, s1: String) {
                callback.onError(s, s1)
            }
        })
    }


    fun createHome(homeName: String, lon : Double, lat : Double, geoName : String?,
                   roomList: List<String>,
                   callback: ITuyaHomeResultCallback) {
        TuyaHomeSdk.getHomeManagerInstance().createHome(homeName,
                lon, lat, geoName, roomList, object : ITuyaHomeResultCallback {
            override fun onSuccess(homeBean: HomeBean) {
                setCurrentHome(homeBean)
                callback.onSuccess(homeBean)
            }

            override fun onError(s: String, s1: String) {
                callback.onError(s, s1)
            }
        })
    }
}