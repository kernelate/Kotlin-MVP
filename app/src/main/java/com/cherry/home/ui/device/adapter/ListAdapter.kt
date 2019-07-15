package com.cherry.home.ui.device.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.cherry.home.data.model.NoImageList
import kotlinx.android.synthetic.main.activity_row.view.*
import javax.inject.Inject

class ListAdapter @Inject constructor(): RecyclerView.Adapter<ListAdapter.AddDeviceViewHolder>() {

    private var listDevice : List<NoImageList>

    private var clickListener: ClickListener? = null

    init {
        listDevice = emptyList()
    }

    fun setDevice(devices: List<NoImageList>)
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
                .inflate(R.layout.activity_row, viewGroup, false)
        return AddDeviceViewHolder(view)
    }




    inner class AddDeviceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        lateinit var selectDevice : String

          var nameDevice : TextView = itemView.child_lbl


//         @BindView(R.id.child_image)
//         @JvmField var button : ImageView? = null
//
//        @BindView(R.id.child_relative)
//        @JvmField var childRelative : RelativeLayout? = null

         init {
             ButterKnife.bind(this, itemView)
             nameDevice?.setOnClickListener {
                 clickListener?.onClickDevice(selectDevice)
             }
         }

         fun bind (device : NoImageList) = with(itemView) {

             selectDevice = device.name
             nameDevice?.text = String.format("%s%s", device.name.substring(0,1).toUpperCase(), device.name.substring(1) )

         }

    }
}