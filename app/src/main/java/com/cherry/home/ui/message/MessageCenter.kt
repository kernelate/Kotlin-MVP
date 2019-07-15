package com.cherry.home.ui.message

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import kotlinx.android.synthetic.main.activity_timelapse.*

class MessageCenter : BaseFragment(), MessageCenterView {
    override fun snackBarLayoutId(): ViewGroup {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        val TAG: String = "MessageCenter"
    }

    fun newInstance(): MessageCenter {
        return MessageCenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewpager()
    }

    override fun layoutId(): Int = R.layout.activity_timelapse


    private fun setupViewpager(){
        tabs.addTab(tabs.newTab().setText("Alarm"))
        tabs.addTab(tabs.newTab().setText("Family"))
        tabs.addTab(tabs.newTab().setText("Notification"))
        tabs!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MessageCenterAdapter(fragmentManager!!, tabs!!.tabCount)
        viewpager!!.adapter = adapter

        viewpager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager!!.currentItem = tab.position
            }

        })
    }
}