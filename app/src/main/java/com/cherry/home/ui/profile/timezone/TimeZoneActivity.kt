package com.cherry.home.ui.profile.timezone

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_timezone.*
import javax.inject.Inject


class TimeZoneActivity : BaseActivity(), TimeZoneView, TimeZoneAdapter.ClickListener {

    private var TAG : String = "TimeZoneActivity"

    @Inject lateinit var timeZoneAdapter: TimeZoneAdapter
    @Inject lateinit var timeZonePresenter: TimeZonePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        timeZonePresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.timezone)

        recyclerView_timezone.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timeZoneAdapter
        }

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search_timezone.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        search_timezone.maxWidth = Integer.MAX_VALUE

        onQueryTextListener()

        timeZoneAdapter.setClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        timeZonePresenter.getTimeZone()
    }

    override fun layoutId(): Int = R.layout.activity_timezone

    override fun snackBarLayoutId(): ViewGroup = child_relative1


    override fun getTimeZone(timeZone: List<String>) {
        timeZoneAdapter.apply {
            setTimeZoneList(timeZone)
            notifyDataSetChanged()
        }
    }

    override fun onClickTimeZone(timeZone: String) {
        goToProfile(timeZone)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeZonePresenter.detachView()
    }

    private fun goToProfile(timeZone: String){
        val returnIntent = Intent()
        returnIntent.putExtra("timeZone", timeZone)
        setResult(Activity.RESULT_OK, returnIntent)
        finishActivity()
    }

    private fun onQueryTextListener(){
        search_timezone.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                timeZonePresenter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                timeZonePresenter.filter(query)
                return false
            }

        })
    }
}