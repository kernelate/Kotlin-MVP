package com.cherry.home

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log
import com.cherry.home.injection.component.AppComponent
import com.cherry.home.injection.component.DaggerAppComponent
import com.cherry.home.injection.module.AppModule
import com.cherry.home.injection.module.NetworkModule
import com.cherry.home.ui.login.LoginActivity
import com.facebook.stetho.Stetho
//import com.orvibo.homemate.api.OrviboApi
//import com.orvibo.homemate.api.OrviboApi.initHomeMateSDK
//import com.orvibo.homemate.api.UserApi
//import com.orvibo.homemate.application.ViHomeApplication
//import com.orvibo.homemate.util.AppTool
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.panel.TuyaPanelSDK
import com.tuya.smart.home.sdk.TuyaHomeSdk
import timber.log.Timber


class CherryHomeApp : MultiDexApplication() {

    private val TAG = "TuyaSmartApp"

    private var appComponent: AppComponent? = null

    companion object {
        operator fun get(context: Context): CherryHomeApp {
            return context.applicationContext as CherryHomeApp
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
            LeakCanary.install(this)
            Sherlock.init(this)
            Traceur.enableLogging()
        }

        //Orbivo Sdk
//        UserApi.setDebugMode(true, false)
//        if(isMainProcess()){
//            OrviboApi.initHomeMateSDK(this)
//            UserApi.initSource("SDK_fimobile")
//            Log.d(TAG,"UserApi.setDebugMode")
//        }

//        Log.d("TAG", "ViHome = " + ViHomeApplication.getContext() + " VihOme = "+ ViHomeApplication.getAppContext())

        //Tuya Sdk
        L.setSendLogOn(true)
        TuyaHomeSdk.init(this)
        TuyaHomeSdk.setOnNeedLoginListener {
            val intent = Intent(this, LoginActivity::class.java)
            if ((it !is Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        if(BuildConfig.DEBUG){
            TuyaHomeSdk.setDebugMode(true)
        } else {
            TuyaHomeSdk.setDebugMode(false)
        }

        TuyaPanelSDK.init(this, "uep7j8ym8ka5gdc4fexf", "88fttu5anrf3tfytv3rqcpxdj7rtwsdt")

    }

    // Needed to replace the component with a test specific one
    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .networkModule(NetworkModule(this))
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }

//    fun getProcessName(context: Context): String {
//        val pid = android.os.Process.myPid()
//        val mActivityManager = context
//                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (appProcess in mActivityManager
//                .runningAppProcesses) {
//            if (appProcess.pid == pid) {
//                return appProcess.processName
//            }
//        }
//        return ""
//    }
//
//    protected fun isMainProcess() : Boolean{
//        val processName : String = AppTool.getProcessName()
//        Log.d(TAG,"processName $processName" )
//        return !TextUtils.isEmpty(processName) && processName == packageName
//
//    }
}
