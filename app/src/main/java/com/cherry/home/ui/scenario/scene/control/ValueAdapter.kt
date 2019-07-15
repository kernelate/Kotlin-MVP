package com.cherry.home.ui.scenario.scene.control

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.cherry.home.R
import javax.inject.Inject

class ValueAdapter @Inject constructor() :
        RecyclerView.Adapter<ValueAdapter.ValueViewHolder>(){

    private val TAG : String = "ValueAdapter"

    private var task : Int = 0

    fun setValue(value : Int){
        task = value
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ValueViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_value, viewGroup, false)

        return ValueViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(task == 0){
            return 0
        } else {
            return 1
        }
    }

    override fun onBindViewHolder(holder: ValueViewHolder, position: Int) {

        val device = task
        holder.bind(device)
    }



    inner class ValueViewHolder (item : View) : RecyclerView.ViewHolder(item){

        private var task : Int = 0

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind (value : Int){
            task = value
        }
    }
}