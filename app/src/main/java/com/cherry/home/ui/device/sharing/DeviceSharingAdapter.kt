package com.cherry.home.ui.device.sharing

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.SharedUserInfoBean
import kotlinx.android.synthetic.main.activity_shared_user_item.view.*
import javax.inject.Inject

class DeviceSharingAdapter @Inject constructor(): RecyclerView.Adapter<DeviceSharingAdapter.DeviceSharingViewHolder>(){


    private var listSharedUser : List<SharedUserInfoBean>

    init {
        listSharedUser = emptyList()
    }

    fun getSharedUser(listSharedUser : List<SharedUserInfoBean>){
        this.listSharedUser = listSharedUser
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): DeviceSharingViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_shared_user_item, viewGroup, false)
        return DeviceSharingViewHolder(view)
    }

    override fun getItemCount(): Int = listSharedUser.size

    override fun onBindViewHolder(holder: DeviceSharingViewHolder, position: Int) {
        val sharedUser = listSharedUser[position]
        holder.bind(sharedUser)
    }

    inner class DeviceSharingViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

         var icon : ImageView = itemView.user_icon

         var tvName : TextView = itemView.user_name

         var tvEmail : TextView = itemView.user_email

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind (sharedUser : SharedUserInfoBean) = with(itemView) {
            Picasso.with(itemView.context).load(sharedUser.iconUrl).into(icon)
            tvName?.text = sharedUser.remarkName
            tvEmail?.text = sharedUser.userName
        }
    }
}