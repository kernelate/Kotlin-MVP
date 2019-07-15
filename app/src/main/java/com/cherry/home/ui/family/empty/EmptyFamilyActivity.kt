package com.cherry.home.ui.family.empty

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import butterknife.OnClick
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.family.add.AddFamilyActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.LoginHelper
import com.cherry.home.util.ProgressUtil
import com.cherry.home.util.ToastUtil
import kotlinx.android.synthetic.main.activity_family_empty.*
import kotlinx.android.synthetic.main.activity_switch.*
import javax.inject.Inject

class EmptyFamilyActivity : BaseActivity(), EmptyFamilyView{

    @Inject
    lateinit var emptyFamilyPresenter: EmptyFamilyPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        emptyFamilyPresenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        emptyFamilyPresenter.detachView()
    }


    @OnClick(R.id.activity_family_logout_btn)
    fun onLogoutClick() {
        emptyFamilyPresenter.logout()
    }


    @OnClick(R.id.activity_family_empty_btn)
    fun onFamilyEmptyClick() {
        val intent = Intent(this, AddFamilyActivity::class.java)
        intent.putExtra(AddFamilyActivity.KEY_EMPTY_FAMILY, true)
        ActivityUtils.startActivity(this, intent, ActivityUtils.ANIMATE_SLIDE_TOP_FROM_BOTTOM,
                false)
    }

    override fun layoutId(): Int  = R.layout.activity_family_empty

    override fun showError(error: String) {
    }

    override fun showLoading() {
        ProgressUtil.showLoading(this, "Please wait...")
    }

    override fun hideLoading() {
        ProgressUtil.hideLoading()
    }

    override fun reLogin() {
        LoginHelper.reLogin(this, false)
    }

    override fun snackBarLayoutId(): ViewGroup = empty_family_id
}