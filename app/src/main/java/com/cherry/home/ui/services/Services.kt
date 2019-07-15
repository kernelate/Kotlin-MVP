package com.cherry.home.ui.services

import android.os.Bundle
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import kotlinx.android.synthetic.main.activity_select_action.*
import kotlinx.android.synthetic.main.activity_timelapse.*

class Services : BaseFragment() {


    companion object {
        val TAG: String = "Services"
    }

    fun newInstance(): Services {
        return Services()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun layoutId(): Int = R.layout.activity_timelapse

    override fun snackBarLayoutId(): ViewGroup = ll_timelapse1
}