package com.cherry.home.ui.scenario.automation.conditionaction

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_child_scene2.view.*
import javax.inject.Inject

class AConActionAdapter @Inject constructor() : RecyclerView.Adapter<AConActionAdapter.NewActionViewHolder>() {

    private var TAG : String = "AConActionAdapter"

    private var listDevice : List<DeviceBean>

    private var clickListener : ClickListener? = null

    init {
        listDevice = emptyList()
    }

    fun setData(devices: List<DeviceBean>) {
        listDevice = devices
    }

    interface ClickListener {
        fun onClickDevice(device: DeviceBean)
    }

    fun setClickListener(clickListener: ClickListener){
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NewActionViewHolder {
        val view = LayoutInflater
                .from(p0.context)
                .inflate(R.layout.activity_child_scene2, p0, false)

        return NewActionViewHolder(view)
    }

    override fun getItemCount(): Int = listDevice.size

    override fun onBindViewHolder(p0: NewActionViewHolder, p1: Int) {
        val device = listDevice[p1]
        p0.bind(device)
    }

    inner class NewActionViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        lateinit var selectDevice : DeviceBean


        var nameDevice : TextView = itemView.child_scene1

        var deviceIcon : ImageView = itemView.scene_device_icon

        var button : TextView = itemView.child_scene2

        var child_scene : RelativeLayout = itemView.rl_child_scene

        init {
//            ButterKnife.bind(this, itemView)

            child_scene?.setOnClickListener {
                clickListener?.onClickDevice(selectDevice)
            }
        }

        fun bind(device : DeviceBean) = with(itemView) {
            selectDevice = device
            Picasso.with(itemView.context).load(device.getIconUrl()).into(deviceIcon)
            nameDevice?.setText(selectDevice.name)

        }
    }
}