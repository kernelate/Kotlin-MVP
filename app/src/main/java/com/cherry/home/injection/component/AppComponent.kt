package com.cherry.home.injection.component

import android.app.Application
import android.content.Context
import com.cherry.home.data.DataManager
import com.cherry.home.data.api.UserApi
import com.cherry.home.data.local.AppPreferenceHelper
import com.cherry.home.injection.ApplicationContext
import com.cherry.home.injection.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun dataManager(): DataManager

    fun userApi(): UserApi

    fun preferenceHelper() : AppPreferenceHelper
}
