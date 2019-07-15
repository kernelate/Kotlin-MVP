package com.cherry.home.ui.room.bedroom

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
import kotlinx.android.synthetic.main.fragment_bedroom.*
import javax.inject.Inject

class BedRoomFragment : BaseFragment(), BedroomView {


    private val TAG : String = "BedRoomFragment"

    @Inject lateinit var bedRoomPresenter: BedRoomPresenter
    @Inject lateinit var bedRoomAdapter: BedRoomAdapter

    companion object {
        val TAG: String = "BedRoomFragment"
    }

    fun newInstance(): BedRoomFragment {
        return BedRoomFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        bedRoomPresenter.attachView(this)
        Log.d(TAG, "oncreate")
    }

    override fun layoutId(): Int = R.layout.fragment_bedroom

    override fun snackBarLayoutId(): ViewGroup = parent_bed

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId(), container, false)
        Log.d(TAG, "onCreateView")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {
            setHasOptionsMenu(true)
        }

        recyclerView_bed.apply {
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(GridItemDecoration(10, 2))
            adapter = bedRoomAdapter
        }

        bedRoomPresenter.init()
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onresume")
        bedRoomPresenter.getRoomDevices()
    }

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(deviceList: List<DeviceBean>) {
        Log.d(TAG, "setdata devicelist ${deviceList.size}")
        bedRoomAdapter.apply {
            setData(deviceList)
            notifyDataSetChanged()
        }
    }
}