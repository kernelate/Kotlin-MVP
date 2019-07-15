package com.cherry.home.injection.module

import com.cherry.home.data.api.UserApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by shivam on 8/7/17.
 */
@Module(includes = arrayOf(NetworkModule::class))
class ApiModule {

    @Provides
    @Singleton
    internal fun providePokemonApi(retrofit: Retrofit): UserApi =
            retrofit.create(UserApi::class.java)
}