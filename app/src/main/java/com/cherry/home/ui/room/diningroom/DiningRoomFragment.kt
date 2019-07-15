package com.cherry.home.ui.room.diningroom

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.ui.main.MainMvpView
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.fragment_diningroom.*
import javax.inject.Inject

class DiningRoomFragment : BaseFragment(), DiningRoomView {


    private val TAG : String = "DiningRoomFragment"

    @Inject lateinit var diningRoomPresenter: DiningRoomPresenter
    @Inject lateinit var diningRoomAdapter: DiningRoomAdapter

    companion object {
        val TAG: String = "DiningRoomFragment"
    }

    fun newInstance(): DiningRoomFragment {
        return DiningRoomFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        diningRoomPresenter.attachView(this)
    }

    override fun layoutId(): Int = R.layout.fragment_diningroom

    override fun snackBarLayoutId(): ViewGroup = parent_dining

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId(), container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {
            setHasOptionsMenu(true)
        }

        recyclerView_dining.apply {
            layoutManager = GridLayoutManager(activity,2)
            addItemDecoration(GridItemDecoration(10,2))
            adapter = diningRoomAdapter
        }

        diningRoomPresenter.init()
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun onResume() {
        super.onResume()
        diningRoomPresenter.getRoomDevices()
    }

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(deviceList: List<DeviceBean>) {
        diningRoomAdapter.apply {
            setData(deviceList)
            notifyDataSetChanged()
        }
    }
}