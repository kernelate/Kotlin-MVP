package com.cherry.home.ui.family.settings

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
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.MemberBean
import kotlinx.android.synthetic.main.activity_child_family_settings.view.*
import javax.inject.Inject

class FamilySettingsAdapter @Inject constructor() : RecyclerView.Adapter<FamilySettingsAdapter.FamilyMembersViewHolder>() {


    private var listMemberBean : List<MemberBean>

    private var clickListener: ClickListener? = null

    init {
        listMemberBean = emptyList()
    }

    fun setHomeList(listMemberBean : List<MemberBean>){
        this.listMemberBean = listMemberBean
    }

    interface ClickListener {
        fun onClickMember(memberId: Long)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): FamilyMembersViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_family_settings, viewGroup, false)
        return FamilyMembersViewHolder(view)
    }

    override fun getItemCount(): Int = listMemberBean.size

    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        val memberBean = listMemberBean[position]
        holder.bind(memberBean)
    }

    inner class FamilyMembersViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var memberBean: MemberBean

        @BindView(R.id.member_child_relative1)
        @JvmField var childRL : RelativeLayout = itemView.member_child_relative1

        @BindView(R.id.member_name)
        @JvmField var tvName : TextView = itemView.member_name

        @BindView(R.id.member_email)
        @JvmField var tvEmail : TextView = itemView.member_email

        @BindView(R.id.member_icon)
        @JvmField var imgIcon : ImageView = itemView.member_icon

        @BindView(R.id.member_admin)
        @JvmField var tvAdmin : TextView = itemView.member_admin

        init {
            ButterKnife.bind(itemView)
            childRL.setOnClickListener {
                clickListener?.onClickMember(memberBean.memberId)
            }
        }

        fun bind(memberBean: MemberBean){
            this.memberBean = memberBean

//            Picasso.with(itemView.context).load(memberBean.headPic).into(imgIcon)
            tvName.text = memberBean.nickName
            tvEmail.text = memberBean.account
            if(memberBean.isAdmin){
                tvAdmin.text = "Administrator"
            }

        }
    }
}