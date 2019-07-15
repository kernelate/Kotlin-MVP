package com.cherry.home.ui.scenario.scene.control

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.cherry.home.R
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import javax.inject.Inject

class SEnumAdapter @Inject constructor() : RecyclerView.Adapter<SEnumAdapter.EnumViewHolder>() {

    private val TAG : String = "SEnumAdapter"

    lateinit var enumList : List<TaskListBean>

    init {
        enumList = emptyList()
    }

    fun setEnum(task : List<TaskListBean>){
        enumList = task
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): EnumViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_bool_enum, viewGroup, false)

        return EnumViewHolder(view)
    }

    override fun getItemCount(): Int {

        return enumList.size
    }

    override fun onBindViewHolder(holder: EnumViewHolder, position: Int) {

        val device = enumList[position]
        holder.bind(device)
    }


    inner class EnumViewHolder (item : View) : RecyclerView.ViewHolder(item) {

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind (enum : TaskListBean) = with(itemView){

        }
    }
}