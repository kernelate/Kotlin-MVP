package com.cherry.home.ui.scenario.automation.function

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean
import kotlinx.android.synthetic.main.activity_child_select_function.view.*
import javax.inject.Inject

class AFunctionAdapter @Inject constructor() : RecyclerView.Adapter<AFunctionAdapter.AutoFunctionViewHolder>() {

    private val TAG : String = "AFunctionAdapter"

    private var listDevice : List<TaskListBean>

    private var clickListener : ClickListener? = null

    init {
        listDevice = emptyList()
    }

    fun setData(function: List<TaskListBean>) {
        listDevice = function
    }


    interface ClickListener {
        fun onClickDevice(device: TaskListBean)
    }

    fun setClicklistener(clickListener: ClickListener){
        this.clickListener = clickListener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutoFunctionViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_select_function, viewGroup, false)

        return AutoFunctionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDevice.size
    }

    override fun onBindViewHolder(holder: AutoFunctionViewHolder, position: Int) {
        val device = listDevice[position]
        holder.bind(device)
    }

    inner class AutoFunctionViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        lateinit var selectDevice : TaskListBean

         var dpsName : TextView = itemView.tv_dps

         var dpsLayout : RelativeLayout = itemView.rl_child_dps

        init {
//            ButterKnife.bind(this, itemView)
            dpsLayout?.setOnClickListener {
                clickListener?.onClickDevice(selectDevice)
            }
        }

        fun bind (device : TaskListBean) = with(itemView) {
            selectDevice = device
            dpsName?.setText(selectDevice.name)

        }
    }
}