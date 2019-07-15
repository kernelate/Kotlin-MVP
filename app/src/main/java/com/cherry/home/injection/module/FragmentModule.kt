package com.cherry.home.injection.module

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.cherry.home.injection.ActivityContext
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(private val fragment: Fragment) {

    @Provides
    internal fun providesFragment(): Fragment = fragment

    @Provides
    internal fun provideActivity(): FragmentActivity? = fragment.activity

    @Provides
    @ActivityContext
    internal fun providesContext(): Context? = fragment.context

}