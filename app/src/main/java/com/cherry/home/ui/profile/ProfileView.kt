package com.cherry.home.ui.profile

import android.net.Uri
import com.cherry.home.ui.base.MvpView
import java.io.File

interface ProfileView : MvpView {

    fun showError(error: String)

    fun setUserProfile(img : String)

    fun setUserNickname(name : String)

    fun setUserEmail(email : String)

    fun setLocation(location : String)

    fun setTimeZone(timeZone : String)

    fun reLogin()

    fun showProgress()

    fun hideProgress()

    fun getRealPathFromURI(contentUri: Uri): File

//    fun setTimeZone(timezone : String)
}
