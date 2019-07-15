package com.cherry.home.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.CursorLoader
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.profile.timezone.TimeZoneActivity
import com.cherry.home.util.*
import com.cherry.home.util.map.MapsActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.editname_dialog.*
import kotlinx.android.synthetic.main.editname_dialog.view.*
import java.io.File
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.*
import javax.inject.Inject

class ProfileActivity : BaseActivity(), ProfileView {


    @Inject
    lateinit var profilePresenter: ProfilePresenter

    private lateinit var nameProfile : String

    companion object {
        val TAG: String = "ProfileActivity"
    }

    fun newInstance(): ProfileActivity {
        return ProfileActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        profilePresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.profile)

        onClick()

        for (id in TimeZone.getAvailableIDs()) {
            Log.d(TAG, "id $id")
            id.indexOf('/').let {
                if (it == -1)
                    null
                else {
                    Log.d(TAG, "${id.substring(it)} | ${id.substring(it + 1)} ")
                }
            }
        }

        profilePresenter.init()
        Log.d(TAG, "picture oncreate")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "picture resume")


    }

    override fun layoutId(): Int = R.layout.activity_profile
    override fun snackBarLayoutId(): ViewGroup = profile_id

    override fun showError(error: String) {
        showSnack(snackBarLayoutId(), error, Snackbar.LENGTH_SHORT)
    }

    override fun setUserProfile(img: String) {
        Log.d(TAG, "picture setUserProfile()  $img")

        Picasso.with(this)
                .load(img)
                .transform(CircleTransform())
                .into(profile_photo)

    }

    override fun setUserNickname(name: String) {
        nameProfile = name
        profile_name_edit.text = nameProfile
    }


    override fun setUserEmail(email: String) {
        profile_email_edit.text = email
    }

    override fun setLocation(location: String) {
        Log.d(TAG, "location $location textview ${getString(R.string.location)}")
        profile_location.text = location
    }

    override fun setTimeZone(timeZone: String) {
        profile_timezone.text = timeZone
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        super.onNetworkConnectionChanged(isConnected)

        when (isConnected) {
            TRUE -> {
                profile_logout.isEnabled = true
                profile_logout.setBackgroundResource(R.drawable.gradient_red)
            }
            FALSE -> {
                profile_logout.isEnabled = false
                profile_logout.setBackgroundResource(R.drawable.gray_button_bg)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "picture onActivityResult")
        profilePresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun reLogin() {
        LoginHelper.reLogin(this, false)
    }

    override fun showProgress() {
        rl_circularProgressbar_profile.visible()
        circularProgressbar_profile.visible()
        circularProgressbar_profile.progress
    }

    override fun hideProgress() {
        rl_circularProgressbar_profile.gone()
        circularProgressbar_profile.gone()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun onClick() {

        ll_profile1.setOnClickListener {
            Log.d(TAG, "picture setonclick")
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val dialog = this?.let { it1 -> BottomSheetDialog(it1) }
            dialog?.setContentView(view)
            dialog?.show()

            view.selectAlbum.setOnClickListener {
                Log.d(TAG, "picture album")
                verifyStoragePermissions(this)
                val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, ProfilePresenter.REQUEST_IMAGE)
                dialog.dismiss()

            }
        }

        profile_logout.setOnClickListener {
            profilePresenter.logOut()
        }

        location.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, ProfilePresenter.REQUEST_LOCATION, 0, false)
        }

        ll_profile2.setOnClickListener {
            showEditName()
        }

        timeZone.setOnClickListener {
            val intent = Intent(this, TimeZoneActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, ProfilePresenter.REQUEST_TIMEZONE, 0, false)
        }
    }

    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        Log.d(TAG, "picture verifystorage")
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    ProfilePresenter.PERMISSIONS_STORAGE,
                    ProfilePresenter.REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun getRealPathFromURI(contentUri: Uri): File {
        Log.d(TAG, "picture getRealPathFromURI")
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = File(cursor?.getString(column_index!!))
        cursor?.close()
        return result
    }


    private fun showEditName() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.editname_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.btn_ok.setOnClickListener {
            mAlertDialog.dismiss()

            if (!mDialogView.et_rename.text.isNullOrBlank()) {
                nameProfile = mDialogView.et_rename.text.toString()
                profile_name_edit.text = nameProfile
                profilePresenter.updateProfileName(nameProfile)
            } else {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            }
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}