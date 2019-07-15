package com.cherry.home.ui.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.family.FamilyManager
import com.cherry.home.ui.family.empty.EmptyFamilyActivity
import com.cherry.home.ui.home.HomeFragment
import com.cherry.home.ui.homemanagement.HomeManagementFragment
import com.cherry.home.ui.profile.ProfileActivity
import com.cherry.home.ui.room.bedroom.BedRoomFragment
import com.cherry.home.ui.room.diningroom.DiningRoomFragment
import com.cherry.home.ui.room.kitchen.KitchenFragment
import com.cherry.home.ui.room.livingroom.LivingRoomFragment
import com.cherry.home.ui.scenario.ScenarioFragment
import com.cherry.home.ui.settings.SettingsFragment
import com.cherry.home.util.ActivityUtils
import com.cherry.home.util.CircleTransform
import com.cherry.home.util.replaceFragment
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_top.*
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvpView, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var mainPresenter: MainPresenter

    private val TAG = MainActivity::class.qualifiedName

    private var navFamily: TextView? = null
    private var navUsername: TextView? = null
    private var navProfile: ImageView? = null
    private var navLinear: LinearLayout? = null

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mainPresenter.attachView(this)

        initToolbar()

        actionBarDrawerToggle = ActionBarDrawerToggle(
                this, drawer_layout, main_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        drawer_layout.bringToFront()
        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment().newInstance(), R.id.container_body)
        }
        initNavHeader()

        mainPresenter.getUsername()

    }

    override fun layoutId(): Int = R.layout.activity_main

    override fun onResume() {
        super.onResume()

        mainPresenter.getProfilepic()
        mainPresenter.getFamily()
    }

    override fun showProgress(show: Boolean) {

    }

    override fun showError(error: Throwable) {

    }

    override fun showError(code: String, msg: String) {
        showSnack(drawer_layout, msg, Snackbar.LENGTH_SHORT, null)
    }

    override fun gotoFamilyActivity() {
        ActivityUtils.gotoActivity(this, EmptyFamilyActivity::class.java, ActivityUtils.ANIMATE_SLIDE_TOP_FROM_BOTTOM, false)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPanelKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                exitConfirmation()
                return true
            }
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                openFragment("HomeFragment")
            }

            R.id.nav_scenario -> {
                openFragment("Scenario")
            }
//            R.id.nav_settings -> {
//                openFragment("SettingsFragment")
//            }
//            R.id.nav_services -> {
//                openFragment("Services")
//            }
            R.id.nav_home_management -> {
                openFragment("HomeManagementFragment")
            }
            R.id.nav_livingroom -> {
                openFragment("LivingRoomFragment")
            }
            R.id.nav_diningroom -> {
                openFragment("DiningRoomFragment")
            }
            R.id.nav_bedroom -> {
                openFragment("BedRoomFragment")
            }
            R.id.nav_kitchen -> {
                openFragment("KitchenFragment")
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun getCurrentHome(): HomeBean? = FamilyManager.getInstance(this).getCurrentHome()

    override fun setFamilyName(name: String) {
        navFamily!!.text = name
    }

    override fun queryHomeList(callback: ITuyaGetHomeListCallback) {
        FamilyManager.getInstance(this).getHomeList(callback)
    }

    override fun snackBarLayoutId(): ViewGroup = drawer_layout

    private fun openFragment(tag: String) {
        when (tag) {
            "HomeFragment" -> {
                if (supportFragmentManager.findFragmentById(R.id.container) == null) {
                    replaceFragment(HomeFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.title_home)
                }
            }
            "Scenario" -> {
                if (supportFragmentManager.findFragmentById(R.id.container) == null) {
                    replaceFragment(ScenarioFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.smart)
                }
            }
            "SettingsFragment" -> {
                if (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) == null) {
                    replaceFragment(SettingsFragment().newInstance(), R.id.container_body)
                }
            }

//            "Services" -> {
//                if (supportFragmentManager.findFragmentByTag(Services.TAG) == null) {
//                    replaceFragment(Services().newInstance(), R.id.container_body)
//                }
//            }
            "HomeManagementFragment" -> {
                if (supportFragmentManager.findFragmentByTag(HomeManagementFragment.TAG) == null) {
                    replaceFragment(HomeManagementFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.home_management)
                }
            }
            "LivingRoomFragment" -> {
                if (supportFragmentManager.findFragmentByTag(LivingRoomFragment.TAG) == null) {
                    replaceFragment(LivingRoomFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.living_room)
                }
            }
            "DiningRoomFragment" -> {
                if (supportFragmentManager.findFragmentByTag(DiningRoomFragment.TAG) == null) {
                    replaceFragment(DiningRoomFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.dining_room)
                }
            }
            "BedRoomFragment" -> {
                if (supportFragmentManager.findFragmentByTag(BedRoomFragment.TAG) == null) {
                    replaceFragment(BedRoomFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.bedRoomFragment)
                }
            }
            "KitchenFragment" -> {
                if (supportFragmentManager.findFragmentByTag(KitchenFragment.TAG) == null) {
                    replaceFragment(KitchenFragment().newInstance(), R.id.container_body)
                    setToolbarTitle(R.string.kitchenFragment)
                }
            }
        }
    }

    override fun showHamburgerIcon() {
        Log.d(TAG, "showHamburgerIcon()")
        main_toolbar.setOnClickListener(null)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()
        main_toolbar.setNavigationOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

    }

    override fun showBackIcon(isFragment: Boolean, fragment: Fragment) {
        Log.d(TAG, "showBackIcon(): isFragment = $isFragment | fragment = ${fragment}")

        if((supportFragmentManager.findFragmentByTag(HomeFragment.TAG) != null) && (supportFragmentManager.findFragmentByTag(ScenarioFragment.TAG) != null)&& (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) != null)&& (supportFragmentManager.findFragmentByTag(ScenarioFragment.TAG) != null)&& (supportFragmentManager.findFragmentByTag(HomeManagementFragment.TAG) != null)){
            actionBarDrawerToggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            main_toolbar.setNavigationOnClickListener {
                if (isFragment) {
                    if (supportFragmentManager.findFragmentById(fragment.id) != null) {
                        Log.d(TAG, "showBackIcon()")
//                    removeFragment(fragment)
                        replaceFragment(HomeFragment().newInstance(), R.id.container_body)
                    }
                } else {
                    onBackPressed()
                }
            }
        }

    }

    override fun setUsername(username: String) {
        navUsername!!.text = username
    }

    fun msgShow(msg: String) {

    }

    private fun initNavHeader() {
        val headerView = nav_view.getHeaderView(0)
        navFamily = headerView.findViewById(R.id.tv_nav_family) as TextView
        navUsername = headerView.findViewById(R.id.tv_nav_email) as TextView
        navProfile = headerView.findViewById(R.id.iv_nav_header) as ImageView
        navLinear = headerView.findViewById(R.id.ll_nav) as LinearLayout

//        navUsername!!.setOnClickListener {
//            Log.d(TAG, "setonclick")
//        }

        navProfile!!.setOnClickListener {
            Log.d(TAG, "setonclick")
//            replaceFragment(ProfileActivity().newInstance(), R.id.container_body)
//            drawer_layout.closeDrawer(GravityCompat.START)
            ActivityUtils.gotoActivity(this, ProfileActivity::class.java, ActivityUtils.ANIMATE_FORWARD, false)
        }

    }

    override fun setProfile( url : String) {
        Picasso.with(this)
                .load(url)
                .transform(CircleTransform())
                .into(navProfile)
    }


}
