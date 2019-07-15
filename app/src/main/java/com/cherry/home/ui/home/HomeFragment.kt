package com.cherry.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.device.add.AddDeviceActivity
import com.cherry.home.ui.device.setting.DeviceSettingActivity
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.ui.main.MainMvpView
import com.cherry.home.util.*
import com.tuya.smart.android.panel.TuyaPanelSDK
import com.tuya.smart.android.panel.api.ITuyaPanelLoadCallback
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import javax.inject.Inject

class HomeFragment : BaseFragment(), HomeMvpView {

    @Inject lateinit var homePresenter: HomePresenter

    @Inject lateinit var homeAdapter: HomeAdapter

    lateinit var devId: String
    internal var pStatus = 0
    private val handler = Handler()

    companion object {
        val TAG: String = "HomeFragment"

        private val INTENT_DEVID: String = "intent_devid"

        fun newInstance(devId: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(INTENT_DEVID, devId)
            fragment.arguments = args

            return fragment
        }
    }

    fun newInstance(): HomeFragment {
        return HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        homePresenter.attachView(this)

        if (arguments != null) {
            devId = arguments!!.getString(HomeFragment.INTENT_DEVID)
        }

    }

    override fun layoutId(): Int  = R.layout.fragment_home

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId(), container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {
            title = getString(R.string.title_home)
            setHasOptionsMenu(true)
        }

        recyclerView?.apply {
            layoutManager = GridLayoutManager(activity,2)
            addItemDecoration(GridItemDecoration(10,2))
//            layoutManager = LinearLayoutManager(context)
            adapter = homeAdapter
        }

        swipeToRefresh?.apply {
            setProgressBackgroundColorSchemeResource(R.color.primary)
            setColorSchemeResources(R.color.white)
            setOnRefreshListener {
                if (NetworkUtil.isNetworkConnected(context)) {
                    homePresenter.getDataFromServer()
                } else {
                    swipeToRefresh?.isRefreshing = false
                    swipeToRefresh?.gone()
                    showSnack(parent_layout, R.string.ty_net_useless_info, Snackbar.LENGTH_INDEFINITE, R.string.retry)
                }
            }
        }

        homeAdapter.onItemClick = {bean->
            homePresenter.onClickDevice(bean)

        }

        homePresenter.init()

        onClick()

        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun onResume() {
        super.onResume()

        homePresenter.getDataFromServer()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.manu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_add_device -> {
                val i = Intent(activity, AddDeviceActivity::class.java)
                startActivity(i)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun loadStart() {
        swipeToRefresh.isRefreshing = true
    }

    override fun loadFinish() {
        swipeToRefresh.isRefreshing = false
    }

    override fun hideBackgroundView() {
        list_background_tip?.gone()
        rl_list?.visible()
    }

    override fun showBackgroundView() {
        rl_list?.gone()
        list_background_tip?.visible()
    }

    override fun gotoCreateHome() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            if (recyclerView?.visibility == View.VISIBLE && homeAdapter.itemCount > 0) {
                swipeToRefresh?.isRefreshing = true
            } else {
                progressBar?.visible()
                recyclerView?.gone()
                swipeToRefresh?.gone()
            }
        } else {
            swipeToRefresh?.isRefreshing = false
            progressBar?.gone()
        }
    }

    override fun showProgress() {
        rl_circularProgressbar.visible()
        circularProgressbar.visible()
        circularProgressbar.progress
    }

    override fun hideProgress() {
        rl_circularProgressbar.gone()
        circularProgressbar.gone()

    }



    override fun showError(error: Throwable) {

    }

    override fun showError(string: String) {
        swipeToRefresh?.gone()
        showSnack(parent_layout, string, Snackbar.LENGTH_LONG, null)
    }

    override fun gotoFamilyActivity() {
    }

    override fun gotoDeviceSetting(devId: String) {

        val i = Intent(activity, DeviceSettingActivity::class.java)
        i.putExtra("devId", devId)
        startActivity(i)
    }

    override fun updateDeviceData(myDevices: List<DeviceBean>) {
        if (homeAdapter != null) {
            homeAdapter?.apply {
                setData(myDevices)
                notifyDataSetChanged()
            }
            recyclerView?.visible()
            swipeToRefresh?.visible()

        }
    }

    override fun gotoPanelViewController(homeID: Long, devId: String, callback: ITuyaPanelLoadCallback) {
        TuyaPanelSDK.getPanelInstance().gotoPanelViewControllerWithDevice(activity, homeID, devId, callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        homePresenter.onDestroy()
        homePresenter.detachView()
    }


    override fun snackBarLayoutId(): ViewGroup = parent_layout

    private fun onClick(){
        tv_empty_func.setOnClickListener {
            val i = Intent(activity, AddDeviceActivity::class.java)
            ActivityUtils.startActivity(activity,i,ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

}