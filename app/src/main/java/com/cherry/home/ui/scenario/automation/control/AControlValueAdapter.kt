package com.cherry.home.ui.scenario.automation.control

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.cherry.home.R
import javax.inject.Inject

class AControlValueAdapter @Inject constructor() : RecyclerView.Adapter<AControlValueAdapter.AutoValueViewHolder>() {

    private val TAG : String = "AControlValueAdapter"

    private var task : Int = 0

    fun setValue(value : Int){
        task = value
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutoValueViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_value, viewGroup, false)

        return AutoValueViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(task == 0){
            return 0
        } else {
            return 1
        }
    }

    override fun onBindViewHolder(holder: AutoValueViewHolder, position: Int) {
        val device = task
        holder.bind(device)
    }

    inner class AutoValueViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

        private var task : Int = 0

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind (value : Int){
            task = value
        }
    }

}