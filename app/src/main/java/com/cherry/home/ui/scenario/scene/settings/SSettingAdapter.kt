package com.cherry.home.ui.scenario.scene.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.ButterKnife
import com.cherry.home.R
import com.cherry.home.ui.scenario.model.DeviceScene
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_child_actions.view.*
import javax.inject.Inject

class SSettingAdapter @Inject constructor() : RecyclerView.Adapter<SSettingAdapter.SceneViewHolder>(){

    private val TAG : String = "SSettingAdapter"
    private var listTask : MutableList<DeviceScene>

    private var clickListener: Clicklistener? = null

    init {
        listTask = ArrayList<DeviceScene>()
    }

    fun setTask(sceneTask: MutableList<DeviceScene>){
        listTask = sceneTask
    }

    interface Clicklistener {
        fun clickAction(sceneTask: DeviceScene)
    }

    fun setClicklistener(clicklistener: Clicklistener){
        this.clickListener = clickListener
    }

    fun removeAt(position: Int) {
        listTask.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): SceneViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_actions, viewGroup, false)

        return SceneViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTask.size
    }

    override fun onBindViewHolder(holder: SceneViewHolder, position: Int) {

        val device = listTask[position]

        holder.bind(device)

    }

    inner class SceneViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

        private lateinit var controlTask: DeviceScene

         var ll_action : LinearLayout = itemView.ll_child_action

         var deviceIcon : ImageView = itemView.iv_action

         var deviceName : TextView = itemView.tv_child_name

         var deviceStatus : TextView = itemView.tv_child_status

         var taskName : TextView = itemView.tv_child_task_name

         var taskValue : TextView = itemView.tv_child_task_value

        init {
            ButterKnife.bind(this, itemView)

            ll_action?.setOnClickListener {
                clickListener?.clickAction(controlTask)
            }
        }

        fun bind (sceneTask : DeviceScene) = with( itemView){
            controlTask = sceneTask
            Picasso.with(itemView.context).load(sceneTask.img).into(deviceIcon)
            deviceName?.text = sceneTask.name

            if(sceneTask.status!!){
                deviceStatus?.text = "Online"
            } else {
                deviceStatus?.text = "Offline"
            }
            taskName?.text = sceneTask.control + ":"

            when(sceneTask.type){
                "bool" ->{
                    taskValue?.text = sceneTask.value
                }
                "value" ->{
                    taskValue?.text = sceneTask.value + "s"
                }
            }
        }
    }
}