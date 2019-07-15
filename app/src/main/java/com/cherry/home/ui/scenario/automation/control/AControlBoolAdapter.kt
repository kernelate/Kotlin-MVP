package com.cherry.home.ui.scenario.automation.control

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import kotlinx.android.synthetic.main.activity_child_bool_enum.view.*
import javax.inject.Inject

class AControlBoolAdapter @Inject constructor() : RecyclerView.Adapter<AControlBoolAdapter.AutoBoolHolder>() {


    private val TAG : String = "AControlBoolAdapter"

    lateinit var task :List<String>

    private var clickListener : ClickListener? = null

    private var lastSelectedPosition = -1

    init {
        task = emptyList()
    }

    fun setValue(value : List<String>){
        task = value
    }

    fun setClicklistener(clickListener: ClickListener){
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onClickDevice(device: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutoBoolHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_bool_enum, viewGroup, false)

        return AutoBoolHolder(view)
    }

    override fun getItemCount(): Int {
        return task.size
    }

    override fun onBindViewHolder(holder: AutoBoolHolder, position: Int) {
        val device = task[position]
        holder.radiobtn?.isChecked = (lastSelectedPosition == position)
        holder.bind(device)
    }

    inner class AutoBoolHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

        lateinit var control : String

         var controlDevice : TextView = itemView.tv_child_control

         var ll_bool : LinearLayout = itemView.ll_child_control

         var radiobtn : RadioButton = itemView.rb_bool

        init {
//            ButterKnife.bind(this, itemView)

            radiobtn.setOnClickListener {
                lastSelectedPosition = adapterPosition
                clickListener?.onClickDevice(control)
                notifyDataSetChanged()
            }

            ll_bool.setOnClickListener {
                lastSelectedPosition = adapterPosition
                clickListener?.onClickDevice(control)
                notifyDataSetChanged()
            }
        }

        fun bind (value : String){
            control = value
            controlDevice.text = value
        }
    }
}