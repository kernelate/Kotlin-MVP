package com.cherry.home.ui.family.settings

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.family.members.FamilyMembersActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.map.MapsActivity
import com.tuya.smart.home.sdk.bean.MemberBean
import kotlinx.android.synthetic.main.activity_family_settings.*
import kotlinx.android.synthetic.main.bottom_sheet_remove_family.view.*
import kotlinx.android.synthetic.main.confirm_family_name.*
import javax.inject.Inject

class FamilySettingsActivity : BaseActivity(), FamilySettingsView, FamilySettingsAdapter.ClickListener {

    @Inject lateinit var familySettingsPresenter: FamilySettingsPresenter
    @Inject lateinit var familySettingsAdapter: FamilySettingsAdapter

    private var homeId : Long = 0

    private lateinit var nameFamily : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        familySettingsPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.family_settings)

        if(intent.extras != null){
            homeId = intent.getLongExtra("homeId", 0)
        }

        recyclerView_family.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = familySettingsAdapter
        }
        onClick()
        familySettingsAdapter.setClickListener(this)
        familySettingsPresenter.init(homeId)

    }

    override fun onResume() {
        super.onResume()
        familySettingsPresenter.init(homeId)
    }

    override fun layoutId(): Int = R.layout.activity_family_settings

    override fun snackBarLayoutId(): ViewGroup = family_settings_id

    override fun showError(error: String) {
        showSnack(snackBarLayoutId(), error, Snackbar.LENGTH_SHORT, null)
    }

    override fun setFamilyName(name: String) {
        nameFamily = name
        edit_family_settings.text = nameFamily
    }

    override fun setLocation(location: String) {
        edit_family_location.text = location
    }

    override fun getMember(memberBeanList: MutableList<MemberBean>) {
        familySettingsAdapter.apply {
            setHomeList(memberBeanList)
            notifyDataSetChanged()
        }
    }

    override fun goToHomeManagement() {
        onBackPressed()
    }

    override fun onClickMember(memberId: Long) {
        val intent = Intent(this, FamilyMembersActivity::class.java)
        intent.putExtra("memberId", memberId)
        intent.putExtra("homeId", homeId)
        ActivityUtils.startActivity(this,intent, ActivityUtils.ANIMATE_SCALE_OUT, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        familySettingsPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        familySettingsPresenter.detachView()
    }

    private fun onClick(){

        ll_family_location.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, Constant.REQUEST_LOCATION, 0, false)
        }

        remove_family.setOnClickListener {
            bottomSheetRemove()
        }

        ll_family_name.setOnClickListener {
            showChangeNameFamily()
        }
    }

    private fun bottomSheetRemove() {
        val dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_remove_family, null)

        dialog.setContentView(bottomSheet)
        dialog.show()

        bottomSheet.cancel_family.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheet.confirm_leave_family.setOnClickListener {
            dialog.dismiss()
            familySettingsPresenter.removeFamily()
        }
    }

    private fun showChangeNameFamily() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirm_family_name, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mAlertDialog.btn_confirm_family.setOnClickListener {
            mAlertDialog.dismiss()

            if (!mAlertDialog.et_rename_family.text.isNullOrBlank()){
                nameFamily = mAlertDialog.et_rename_family.text.toString()
                edit_family_settings.text = nameFamily
                familySettingsPresenter.updateFamilyName(nameFamily)
            }
        }
    }


}