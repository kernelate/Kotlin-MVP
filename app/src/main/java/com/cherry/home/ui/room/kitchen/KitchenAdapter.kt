package com.cherry.home.ui.room.kitchen

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_row_text2.view.*
import java.text.FieldPosition
import javax.inject.Inject

class KitchenAdapter @Inject constructor() : RecyclerView.Adapter<KitchenAdapter.KitchenViewHolder>() {

    private val TAG : String = "KitchenAdapter"

    private var listDevice : List<DeviceBean>

    var onItemClick : ((DeviceBean)-> Unit)?=null

    init {
        listDevice = emptyList()
    }

    fun setData(devices: List<DeviceBean>) {
        listDevice = devices
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): KitchenViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.activity_row_text2, parent, false)
        return KitchenViewHolder(view)
    }

    override fun getItemCount(): Int = listDevice.size

    override fun onBindViewHolder(holder: KitchenViewHolder, position: Int) {
        val device = listDevice[position]
        holder.bind(device)
    }

    inner class KitchenViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

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