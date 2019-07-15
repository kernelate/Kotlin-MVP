package com.cherry.home.ui.family.members

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cherry.home.injection.ConfigPersistent
import com.cherry.home.ui.base.BasePresenter
import com.tuya.smart.android.user.api.IBooleanCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.bean.MemberBean
import com.tuya.smart.home.sdk.callback.ITuyaGetMemberListCallback
import com.tuya.smart.sdk.api.IResultCallback
import java.io.File
import javax.inject.Inject

@ConfigPersistent
class FamilyMembersPresenter @Inject constructor() : BasePresenter<FamilyMembersView>() {

    private var TAG : String = "FamilyMembersPresenter"
    private var ERROR_PERMISSION_VALIDATE_FAILED = "PERMISSION_VALIDATE_FAILED"

    private lateinit var memberBean: MemberBean
    private lateinit var iTuyaUser : User

    private var memberId : Long = 0
    private var homeId : Long = 0
    private var admin : Boolean = false

    companion object {
        internal val REQUEST_IMAGE : Int = 1
        internal val REQUEST_EXTERNAL_STORAGE = 1
        internal val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun init(memberId : Long, homeId : Long){
        checkViewAttached()
        queryMemberList(homeId, memberId)

        this.memberId = memberId
        this.homeId = homeId
        iTuyaUser = TuyaHomeSdk.getUserInstance().user!!
        getUserInfo()

    }

    fun  getUserInfo(){
        mvpView?.apply {
            if (!iTuyaUser.headPic.isNullOrBlank()) {
                setPicture(iTuyaUser.headPic)
            }
        }
    }


    fun updateMemberRole(name: String, admin : Boolean){
        TuyaHomeSdk.getMemberInstance().updateMember(memberId, name, admin , object : IResultCallback{
            override fun onSuccess() {
                queryMemberList(homeId, memberId)
            }

            override fun onError(code: String, error: String) {
                if(code == ERROR_PERMISSION_VALIDATE_FAILED){
                    mvpView?.setToggleEnable(false)
                } else {
                    mvpView?.setToggleEnable(true)
                    mvpView?.showError(error)
                }
            }

        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_IMAGE ->{
                if(resultCode == Activity.RESULT_OK && data != null){
                    val URI  = data.data
                    val path : File = mvpView?.getRealPathFromURI(URI)!!
                    uploadPhoto(path)
                }
            }
        }
    }

    private fun queryMemberList(homeId : Long, memberId: Long){
        TuyaHomeSdk.getMemberInstance().queryMemberList(homeId, object : ITuyaGetMemberListCallback {
            override fun onSuccess(memberBeanList: MutableList<MemberBean>) {
                if(memberBeanList != null){
                    for(member in memberBeanList){
                        if(member.homeId == memberId){
                            mvpView?.apply {
                                setName(member.nickName)
                                setEmail(member.account)
                                if(!member.headPic.isNullOrBlank()){
                                    setPicture(member.headPic)
                                }
                                setAdmin(member.isAdmin)
                            }
                        }
                    }
                }
            }
            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
            }
        })
    }

    private fun uploadPhoto(file: File){
        Log.d(TAG, "photo  file $file")
        mvpView?.showProgress()
        TuyaHomeSdk.getMemberInstance().uploadMemberAvatar(file.absolutePath, file, object : IBooleanCallback {
            override fun onSuccess(){
                Log.d(TAG, "photo  file $file")
                queryMemberList(homeId, memberId)
                mvpView?.setPicture(memberBean.headPic)
                mvpView?.hideProgress()
            }
            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
                mvpView?.hideProgress()
                Log.d(TAG, "1name1 code $code error $error file  $file")
            }
        })
    }

    fun updateName(name : String) {
        Log.d(TAG, "1name $name admin $admin")
        TuyaHomeSdk.getMemberInstance().updateMember(memberId, name, admin, object : IResultCallback {
            override fun onSuccess() {
                Log.d(TAG, "1name $name admin $admin ")
            }

            override fun onError(code: String, error: String) {
                mvpView?.showError(error)
                Log.d(TAG, "1name code $code error $error memberId  $memberId  name $name  admin $admin")
            }
        })
    }
}