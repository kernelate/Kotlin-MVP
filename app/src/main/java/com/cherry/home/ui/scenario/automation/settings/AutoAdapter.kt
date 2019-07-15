package com.cherry.home.ui.scenario.automation.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.cherry.home.ui.scenario.model.DeviceScene
import com.tuya.smart.sdk.bean.DeviceBean
import kotlinx.android.synthetic.main.activity_child_actions_auto.view.*
import javax.inject.Inject

class AutoAdapter @Inject constructor() : RecyclerView.Adapter<AutoAdapter.AutoViewHolder>() {

    private var TAG : String = "AutoAdapter"

    private var listCondition : MutableList<DeviceScene>

    private var clickListener: Clicklistener? = null

    init {
        listCondition = ArrayList<DeviceScene>()
    }

    fun setAuto(listCondition: MutableList<DeviceScene>){
        this.listCondition = listCondition
    }

    interface Clicklistener {
        fun clickCondition(sceneCondition: DeviceScene)
    }

    fun setClicklistener(clicklistener: Clicklistener){
        this.clickListener = clickListener
    }

    fun removeAt(position: Int) {
        listCondition.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutoViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_actions_auto,viewGroup, false)

        return AutoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listCondition.size
    }

    override fun onBindViewHolder(p0: AutoViewHolder, p1: Int) {
        val device = listCondition[p1]
        p0.bind(device)
    }


    inner class AutoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var controlTask: DeviceScene
        private lateinit var deviceBean: DeviceBean

        var ll_child_id : LinearLayout = itemView.ll_child_action_auto

        var deviceIcon : ImageView = itemView.iv_action

        var ll_action_auto : LinearLayout = itemView.ll_action_auto

        var deviceTask : TextView = itemView.tv_task_auto

        var deviceValue : TextView = itemView.tv_value_auto

       var deviceName : TextView = itemView.tv_device_name_auto

        fun bind(sceneCondition: DeviceScene) = with(itemView){
            controlTask = sceneCondition

            deviceName?.text = sceneCondition.name

            if(sceneCondition.type != "modified"){
                deviceTask?.text = sceneCondition.control + ":"
                deviceValue?.text = sceneCondition.value
            } else {
                deviceTask?.text = sceneCondition.control
            }


        }
    }

}