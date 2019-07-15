package com.cherry.home.ui.homemanagement

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.family.add.AddFamilyActivity
import com.cherry.home.ui.family.settings.FamilySettingsActivity
import com.cherry.home.ui.main.MainMvpView
import com.cherry.home.util.ActivityUtils
import com.tuya.smart.home.sdk.bean.HomeBean
import kotlinx.android.synthetic.main.fragment_home_management.*
import javax.inject.Inject

class HomeManagementFragment : BaseFragment(), HomeManagementView, HomeManagementAdapter.ClickListener{


    @Inject lateinit var homeManagementPresenter: HomeManagementPresenter
    @Inject lateinit var homeManagementAdapter: HomeManagementAdapter

    companion object {
        val TAG: String = "HomeManagementFragment"
    }

    fun newInstance(): HomeManagementFragment {
        return HomeManagementFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        homeManagementPresenter.attachView(this)

        activity?.apply {
            title = getString(R.string.home_management)
            setHasOptionsMenu(false)
        }

    }

    override fun layoutId(): Int = R.layout.fragment_home_management

    override fun snackBarLayoutId(): ViewGroup = home_management_id

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeManagementAdapter
        }
        onClick()

        homeManagementAdapter.setClickListener(this)
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun onResume() {
        super.onResume()
        homeManagementPresenter.queryHome()
    }

    override fun showError(error: String) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    override fun getDataFromServer(homeBeanList: MutableList<HomeBean>) {
        homeManagementAdapter.apply {
            setHomeList(homeBeanList)
            notifyDataSetChanged()
        }
    }

    override fun onClickHome(homeId : Long) {
        val i = Intent(activity, FamilySettingsActivity::class.java)
        i.putExtra("homeId", homeId)
        ActivityUtils.startActivity(activity, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
    }

    private fun onClick(){
        add_family.setOnClickListener {
            val i = Intent(activity, AddFamilyActivity::class.java)
            ActivityUtils.startActivity(activity, i, ActivityUtils.ANIMATE_SCALE_OUT, false)
        }
    }

}