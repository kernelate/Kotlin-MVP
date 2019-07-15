package com.cherry.home.injection.module

import android.content.Context
import com.cherry.home.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by shivam on 8/7/17.
 */

@Module
class NetworkModule(private val context: Context) {


    private fun getBaseUrl() = BuildConfig.hostAPI
    private val REQUEST_TIMEOUT = 60L

    @Provides
    @Singleton
    internal fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
//                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

    @Provides
    @Singleton
    internal fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor,
                                     chuckInterceptor: ChuckInterceptor,
                                     stethoInterceptor: StethoInterceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
//                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addInterceptor(httpLoggingInterceptor)
            httpClientBuilder.addInterceptor(chuckInterceptor)
            httpClientBuilder.addNetworkInterceptor(stethoInterceptor)
        }


        return httpClientBuilder.build()

    }

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
                Timber.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    internal fun provideChuckInterceptor(): ChuckInterceptor = ChuckInterceptor(context)

    @Provides
    @Singleton
    internal fun provideStetho(): StethoInterceptor = StethoInterceptor()

    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    internal fun provideGson(): Gson = GsonBuilder()
            .setLenient()
            .create()

}