package com.cherry.home.ui.device.location

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.tuya.smart.home.sdk.bean.RoomBean
import kotlinx.android.synthetic.main.view_item.view.*
import javax.inject.Inject

class DeviceLocationAdapter @Inject constructor() : RecyclerView.Adapter<DeviceLocationAdapter.DeviceViewHolder>(){

    private var roomList : MutableList<RoomBean>

    private lateinit var clickListener: ClickListener

    private var lastSelectedPosition : Int = -1

    init {
        roomList = arrayListOf()
    }

    fun setItem(roomList : MutableList<RoomBean>){
        this.roomList = roomList
    }

    interface ClickListener {
        fun onClickItem(item: RoomBean)
    }

    fun setClickListener(clickListener : ClickListener){
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DeviceLocationAdapter.DeviceViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.view_item, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int = roomList.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = roomList[position]
        holder.radiobtn?.isChecked = (lastSelectedPosition == position)
        holder.bind(device)
    }


    inner class DeviceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        lateinit var selectDevice : RoomBean

         var parentLayout : RelativeLayout = itemView.parent_layout

         var radiobtn : RadioButton = itemView.radio

         var radioName : TextView = itemView.radio_name

        init {
            ButterKnife.bind(this, itemView)

            parentLayout?.setOnClickListener {
                lastSelectedPosition = adapterPosition
                clickListener?.onClickItem(selectDevice)
                notifyDataSetChanged()
            }

            radiobtn?.setOnClickListener {
                lastSelectedPosition = adapterPosition
                clickListener?.onClickItem(selectDevice)
                notifyDataSetChanged()
            }
        }

        fun bind (device : RoomBean) = with(itemView){
            selectDevice = device
            radioName?.text = device.name
        }

    }
}