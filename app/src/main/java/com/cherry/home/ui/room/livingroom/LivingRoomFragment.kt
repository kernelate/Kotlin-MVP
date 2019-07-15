package com.cherry.home.ui.room.livingroom

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import com.cherry.home.R
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.device.add.AddDeviceActivity
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.ui.main.MainMvpView
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.fragment_livingroom.*
import javax.inject.Inject

class LivingRoomFragment : BaseFragment(), LivingRoomView {

    private val TAG : String = "LivingRoomFragment"

    @Inject lateinit var livingRoomPresenter: LivingRoomPresenter
    @Inject lateinit var livingRoomAdapter: LivingRoomAdapter

    companion object {
        val TAG: String = "LivingRoomFragment"
    }

    fun newInstance(): LivingRoomFragment {
        return LivingRoomFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        livingRoomPresenter.attachView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId(), container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.apply {
            setHasOptionsMenu(true)
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity,2)
            addItemDecoration(GridItemDecoration(10,2))
            adapter = livingRoomAdapter
        }

        livingRoomPresenter.init()
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun layoutId(): Int = R.layout.fragment_livingroom

    override fun snackBarLayoutId(): ViewGroup = parent_living

    override fun onResume() {
        super.onResume()
        livingRoomPresenter.getRoomDevices()
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

    override fun showProgress(show: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(deviceList: List<DeviceBean>) {
        Log.d(TAG, "setData ${deviceList.size}")
        livingRoomAdapter.apply {
            setData(deviceList)
            notifyDataSetChanged()
        }
    }

}