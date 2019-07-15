package com.cherry.home.ui.home

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_row_text.view.*
import javax.inject.Inject

class HomeAdapter @Inject constructor(): RecyclerView.Adapter<HomeAdapter.DeviceViewHolder>() {

    private var listDevice : List<DeviceBean>

    var onItemClick : ((DeviceBean)-> Unit)?=null

    init {
        listDevice = emptyList()
    }

    fun setData(devices: List<DeviceBean>) {
        listDevice = devices
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HomeAdapter.DeviceViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_row_text, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int = listDevice.size

    override fun onBindViewHolder(holder: HomeAdapter.DeviceViewHolder, position: Int) {
        val device = listDevice[position]
        holder.bind(device)
    }

    inner class DeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var selectDevice : DeviceBean

         var nameDevice : TextView = itemView.child_name1

         var statusDevice : TextView = itemView.child_name2

         var iconDevice : ImageView = itemView.device_icon

        init {
            ButterKnife.bind(this, itemView)
            itemView?.setOnClickListener {
                onItemClick?.invoke(selectDevice)
            }
        }

        fun bind (device : DeviceBean) = with(itemView) {
            Picasso.with(itemView.context).load(device.getIconUrl()).into(iconDevice)
            selectDevice = device
            nameDevice?.setText(selectDevice.name)
            if(selectDevice.isOnline){
                statusDevice?.setText("Online")
            } else {
                statusDevice?.setText("Offline")
            }
        }
    }
}