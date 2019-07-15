package com.cherry.home.ui.scenario.automation.action

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

class AActionAdapter @Inject constructor() : RecyclerView.Adapter<AActionAdapter.AutoActionViewHolder>() {

    private var TAG : String = "AActionAdapter"

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

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutoActionViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_scene2, viewGroup, false)

        return AutoActionViewHolder(view)
    }

    override fun getItemCount(): Int = listDevice.size

    override fun onBindViewHolder(holder: AutoActionViewHolder, position: Int) {
        val device = listDevice[position]
        holder.bind(device)
    }

    inner class AutoActionViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        lateinit var selectDevice : DeviceBean

        var nameDevice : TextView = itemView.child_scene1

        var deviceIcon : ImageView = itemView.scene_device_icon

        var button : TextView = itemView.child_scene2

        init {
            itemView?.setOnClickListener {
                clickListener?.onClickDevice(selectDevice)
            }
        }

        fun bind (device : DeviceBean) = with(itemView) {
            Picasso.with(itemView.context).load(device.getIconUrl()).into(deviceIcon)
            selectDevice = device
            nameDevice.text = device.name

        }
    }
}