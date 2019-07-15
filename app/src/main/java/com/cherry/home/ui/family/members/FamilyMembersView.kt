package com.cherry.home.ui.family.members

import android.net.Uri
import com.cherry.home.ui.base.MvpView
import java.io.File

interface FamilyMembersView : MvpView {


    fun showError(error : String)

    fun setName(name : String)

    fun setPicture(image : String)

    fun setEmail(email : String)

    fun setAdmin(status : Boolean)

    fun getRealPathFromURI(contentUri: Uri): File

    fun setToggleEnable(status : Boolean)

    fun showProgress()

    fun hideProgress()
}