package com.cherry.home.ui.scenario.scene

import android.os.Bundle
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_child_timelapse.*
import javax.inject.Inject

class TimeLapseActivity : BaseActivity(), TimeLapseView{

    @Inject lateinit var timeLapsePresenter: TimeLapsePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

    }

    override fun layoutId(): Int = R.layout.activity_child_timelapse

    override fun snackBarLayoutId(): ViewGroup = rl_child_timelapse

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




}