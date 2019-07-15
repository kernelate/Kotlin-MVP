package com.cherry.home.ui.room.bedroom

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_row_text2.view.*
import javax.inject.Inject

class BedRoomAdapter @Inject constructor() : RecyclerView.Adapter<BedRoomAdapter.BedRoomViewHolder>() {

    private val TAG : String = "BedRoomAdapter"

    private var listDevice : List<DeviceBean>

    var onItemClick : ((DeviceBean)-> Unit)?=null

    init {
        Log.d(TAG, "devices init")
        listDevice = emptyList()
    }

    fun setData(devices: List<DeviceBean>) {
        Log.d(TAG, "devices setdata")
        listDevice = devices
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BedRoomViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_row_text2, parent, false)
        return BedRoomViewHolder(view)
    }

    override fun getItemCount(): Int = listDevice.size

    override fun onBindViewHolder(holder: BedRoomViewHolder, position: Int) {
        val device = listDevice[position]
        holder.bind(device)
    }

    inner class BedRoomViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

        lateinit var selectDevice : DeviceBean

        var nameDevice : TextView = itemView.room_name1
        var statusDevice : TextView = itemView.room_name2

        var iconDevice : ImageView = itemView.room_icon

        init {
            itemView?.setOnClickListener {
                onItemClick?.invoke(selectDevice)
            }
        }

        fun bind (device : DeviceBean){
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