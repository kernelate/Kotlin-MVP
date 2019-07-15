package com.cherry.home.ui.aboutus

import android.os.Bundle
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_aboutus.*

class AboutUs : BaseFragment() {


    companion object {
        val TAG: String = "AboutUs"
    }

    fun newInstance(): AboutUs {
        return AboutUs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun layoutId(): Int = R.layout.fragment_aboutus

    override fun snackBarLayoutId(): ViewGroup = frame_about
}