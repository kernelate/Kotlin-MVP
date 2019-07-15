package com.cherry.home.ui.scenario.autosettings.timeperiod.repeat

import android.os.Bundle
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_repeat.*

class RepeatActivity : BaseActivity(), RepeatView{


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        initToolbar()
        setToolbarTitle(R.string.repeat)

    }

    override fun layoutId(): Int = R.layout.activity_repeat

    override fun snackBarLayoutId(): ViewGroup = rl_repeat

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}