package com.cherry.home.ui.device.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.cherry.home.data.model.Category
import kotlinx.android.synthetic.main.activity_row_item.view.*
import javax.inject.Inject

class DeviceAdapter @Inject constructor(): RecyclerView.Adapter<DeviceAdapter.AddDeviceViewHolder>() {

    private var listDevice : List<Category>

    private var clickListener: ClickListener? = null

    init {
        listDevice = emptyList()
    }

    fun setDevice(devices: List<Category>)
    {
        listDevice = devices
    }

    interface ClickListener {
        fun onClickDevice(device: String)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    override fun onBindViewHolder(holder: AddDeviceViewHolder, position: Int) {

        val device = listDevice[position]

        holder.bind(device)


    }

    override fun getItemCount(): Int {

        return listDevice.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AddDeviceViewHolder
    {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_row_item, viewGroup, false)
        return AddDeviceViewHolder(view)
    }




    inner class AddDeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        lateinit var selectDevice : String

          var nameDevice : TextView = itemView.child_name

          var deviceIcon : ImageView = itemView.icon

          var button : ImageView = itemView.child_image


         init {
             ButterKnife.bind(this, itemView)
             // nagana
//             dpsName?.setOnClickListener {
//                 clickListener?.onClickDevice(selectDevice)
//             }
             itemView?.setOnClickListener {
                 clickListener?.onClickDevice(selectDevice)
             }
         }

         fun bind (device : Category) = with(itemView) {

             selectDevice = device.name
             nameDevice?.text = String.format("%s%s", device.name.substring(0,1).toUpperCase(), device.name.substring(1) )
             deviceIcon?.setBackgroundResource(device.icon)

         }

    }
}