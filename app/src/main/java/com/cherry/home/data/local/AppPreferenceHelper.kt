package com.cherry.home.data.local

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.cherry.home.app.Constant
import com.cherry.home.injection.ApplicationContext
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.HomeBean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceHelper @Inject constructor(@ApplicationContext context: Context) : PreferenceHelper {

    private val mPrefs: SharedPreferences

    companion object {
        private val PREF_HOME_ID = "pref_home_id"
        private val PREF_UID = "pref_uid"
        private val PREF_USERNAME = "pref_username"
        private val PREF_SCENE_COVER = "pref_scene_cover"
        open val PREF_CONFIG_MODE = "pref_config_mode"
        open val PREF_CONFIG_PASSWORD = "pref_config_password"
        open val PREF_CONFIG_SSID = "pref_config_ssid"
        private val CURRENT_FAMILY_SUFFIX = "currentHome_"
    }

    init {
        mPrefs = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
    }

    var userName: String
        get() = mPrefs.getString(PREF_USERNAME, Constant.USERNAME)
        set(value) = mPrefs.edit().putString(PREF_USERNAME, value).apply()

    var sceneCover: String
        get() = mPrefs.getString(PREF_SCENE_COVER, Constant.SCENE_COVER)
        set(value) = mPrefs.edit().putString(PREF_SCENE_COVER, value).apply()

    override fun getPrefHomeID(): Long = mPrefs.getLong(PREF_HOME_ID, Constant.HOME_ID)

    override fun getPrefUID(): String = mPrefs.getString(PREF_UID, Constant.UID)

    override fun getPrefMode(): Int = mPrefs.getInt(PREF_CONFIG_MODE, Constant.MODE)

    override fun putCurrentHome(homeBean: HomeBean) {
        if (null == homeBean) {
            return
        }
        val editor = mPrefs.edit()
        var userId: String? = null
        val user = TuyaHomeSdk.getUserInstance().user
        if (null != user) {
            userId = user.uid
        }
        editor.putString(CURRENT_FAMILY_SUFFIX + userId!!, JSON.toJSONString(homeBean))
        editor.commit()
    }

    override fun getCurrentHome(): HomeBean? {
        var userId: String? = null
        val user = TuyaHomeSdk.getUserInstance().user
        if (null != user) {
            userId = user.uid
        }

        Log.d("AppPreferenceHelper ", " AppPreferenceHelper 11 $userId")

        val currentFamilyStr: String = mPrefs.getString(CURRENT_FAMILY_SUFFIX + userId!!, "")

        if (TextUtils.isEmpty(currentFamilyStr)) {
            return null
        }

        return JSON.parseObject(currentFamilyStr, HomeBean::class.java)
    }

}