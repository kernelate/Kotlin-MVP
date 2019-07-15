package com.cherry.home.ui.family.members

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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.util.CircleTransform
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_family_members.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.editname_dialog.view.*
import java.io.File
import javax.inject.Inject

class FamilyMembersActivity : BaseActivity(), FamilyMembersView {

    private val TAG : String = "FamilyMembersActivity"

    private lateinit var name : String

    @Inject
    lateinit var familyMembersPresenter: FamilyMembersPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        familyMembersPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.family_members)

        if (intent.extras != null) {
            val memberId = intent.getLongExtra("memberId", 0)
            val homeId = intent.getLongExtra("homeId", 0)
            familyMembersPresenter.init(memberId, homeId)
        }
        onClick()

    }

    override fun layoutId(): Int = R.layout.activity_family_members

    override fun snackBarLayoutId(): ViewGroup = family_members_id

    override fun setAdmin(status: Boolean) {
        toggleButton1.isChecked = status
    }

    override fun setName(name: String) {
        this.name = name
        family_members_nickname.text = name
    }

    override fun setPicture(image: String) {
        Log.d(TAG, "setPicture : $image")
        Picasso.with(this)
                .load(image)
                .transform(CircleTransform())
                .into(iv_profile)
    }

    override fun setEmail(email: String) {
        family_members_email.text = email
    }

    override fun getRealPathFromURI(contentUri: Uri): File {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = File(cursor?.getString(column_index!!))
        cursor?.close()
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        familyMembersPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showError(error: String) {
        showSnack(snackBarLayoutId(), error, Snackbar.LENGTH_SHORT)
    }

    override fun setToggleEnable(status: Boolean) {
        toggleButton1.isEnabled = status
    }

    override fun showProgress() {
        rl_circularProgressbar_members.visible()
        circularProgressbar_members.visible()
        circularProgressbar_members.progress
    }
    override fun hideProgress() {
        rl_circularProgressbar_members.gone()
        circularProgressbar_members.gone()
    }

    private fun onClick() {
        ll_family_members_photo.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val dialog = this.let { it1 -> BottomSheetDialog(it1) }
            dialog.setContentView(view)
            dialog.show()

            view.selectAlbum.setOnClickListener {
                Log.d(TAG, "album album")
                verifyStoragePermissions(this)
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, FamilyMembersPresenter.REQUEST_IMAGE)

                dialog.dismiss()

            }
        }

        ll_family_members_name.setOnClickListener {
            showFamilyName()
        }

        toggleButton1.setOnCheckedChangeListener { compoundButton, isChecked ->
            familyMembersPresenter.updateMemberRole(name, isChecked)
        }
    }

    private fun showFamilyName() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.editname_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.btn_ok.setOnClickListener {
            mAlertDialog.dismiss()

            if (!mDialogView.et_rename.text.isNullOrBlank() ){
                name = mDialogView.et_rename.text.toString()
                family_members_nickname.text = name
                familyMembersPresenter.updateName(name)
            }
//            var name = mDialogView.et_rename.text.toString()
//
//            if (!name.isNullOrBlank()){
//                familyMembersPresenter.updateMemberRole(name, toggleButton1.isChecked)
//            }else{
//                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
//            }


        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


    private fun verifyStoragePermissions(activity : Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    FamilyMembersPresenter.PERMISSIONS_STORAGE,
                    FamilyMembersPresenter.REQUEST_EXTERNAL_STORAGE
            )
        }
    }


}