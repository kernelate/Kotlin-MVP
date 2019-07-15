package com.cherry.home.ui.homemanagement

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cherry.home.R
import com.tuya.smart.home.sdk.bean.HomeBean
import kotlinx.android.synthetic.main.activity_homemanagement_item.view.*
import javax.inject.Inject

class HomeManagementAdapter @Inject constructor(): RecyclerView.Adapter<HomeManagementAdapter.DeviceSharingViewHolder>()  {

    private var listHomeBean : List<HomeBean>

    private var clickListener: ClickListener? = null

    init {
        listHomeBean = emptyList()
    }

    fun setHomeList(listHomeBean : List<HomeBean>){
        this.listHomeBean = listHomeBean
    }

    interface ClickListener {
        fun onClickHome(homeId: Long)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): DeviceSharingViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_homemanagement_item, viewGroup, false)
        return DeviceSharingViewHolder(view)
    }

    override fun getItemCount(): Int = listHomeBean.size

    override fun onBindViewHolder(holder: DeviceSharingViewHolder, position: Int) {
        val homeBean = listHomeBean[position]
        holder.bind(homeBean)
    }


    inner class DeviceSharingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var homeBean: HomeBean

        var tvName : TextView = itemView.homebean_name

        init {
            itemView.setOnClickListener {
                clickListener?.onClickHome(homeBean.homeId)
            }
        }

        fun bind(homeBean: HomeBean){
            this.homeBean = homeBean
            tvName?.text = homeBean.name
        }
    }
}