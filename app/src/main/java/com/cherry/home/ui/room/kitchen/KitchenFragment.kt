package com.cherry.home.ui.room.kitchen

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.ui.main.MainMvpView
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.fragment_kitchen.*
import javax.inject.Inject

class KitchenFragment : BaseFragment(), KitchenView {

    private val TAG : String = "KitchenFragment"

    @Inject lateinit var kitchenPresenter: KitchenPresenter

    @Inject lateinit var kitchenAdapter: KitchenAdapter

    companion object {
        val TAG: String = "KitchenFragment"
    }

    fun newInstance(): KitchenFragment {
        return KitchenFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        kitchenPresenter.attachView(this)
        Log.d(TAG, "oncreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId(), container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        kitchenPresenter.getRoomDevices()
    }

    override fun layoutId(): Int = R.layout.fragment_kitchen

    override fun snackBarLayoutId(): ViewGroup = parent_kitchen

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {
            setHasOptionsMenu(true)
        }

        recyclerView_kitchen.apply {
            layoutManager = GridLayoutManager(activity,2)
            addItemDecoration(GridItemDecoration(10,2))
            adapter = kitchenAdapter
        }

        kitchenPresenter.init()
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(deviceList: List<DeviceBean>) {
        kitchenAdapter.apply {
            setData(deviceList)
            notifyDataSetChanged()
        }
    }
}