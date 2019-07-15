package com.cherry.home.ui.scenario.autosettings.timeperiod

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.autosettings.timeperiod.repeat.RepeatActivity
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.map.MapsActivity
import kotlinx.android.synthetic.main.activity_valid_time_period.*
import javax.inject.Inject
import android.location.Geocoder
import java.util.*


class TimePeriodActivity : BaseActivity(), TimePeriodView {


    private var TAG : String = "TimePeriodActivity"

    @Inject lateinit var timePeriodPresenter: TimePeriodPresenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        timePeriodPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.valid_time)

        Log.d(TAG, "oncreate")

        onClick()
        timePeriodPresenter.init()


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_next, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId) {
        R.id.save_smart_settings -> {

            true
        } else -> {

            super.onOptionsItemSelected(item)
        }
    }

    override fun layoutId(): Int = R.layout.activity_valid_time_period

    override fun snackBarLayoutId(): ViewGroup = rl_time_period

    override fun setLocation(location: String) {

    }


    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        showSnack(snackBarLayoutId(), error, Snackbar.LENGTH_SHORT, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        timePeriodPresenter.onActivityResult(requestCode, resultCode, data )
    }

    fun onClick(){

        tv_repeat.setOnClickListener {
            val i = Intent(this, RepeatActivity :: class.java)
            ActivityUtils.startActivity(this, i, ActivityUtils.ANIMATE_FORWARD, false)
        }

        ll_time_period.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, Constant.REQUEST_LOCATION, 0, false)
        }
    }

    override fun getCityName(latitude : Double, longitude : Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 2)
        val cityName = addresses[0].locality
        Log.d(TAG, "cityName $cityName")
        tv_current_city.text = cityName
    }
}