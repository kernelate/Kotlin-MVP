package com.cherry.home.ui.profile.timezone

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cherry.home.R
import kotlinx.android.synthetic.main.activity_child_timezone.view.*
import javax.inject.Inject

class TimeZoneAdapter @Inject constructor() : RecyclerView.Adapter<TimeZoneAdapter.TimeZoneViewHolder>() {

    private var TAG : String = "TimeZoneAdapter"

    private var listTimezone : List<String>

    private var clickListener: ClickListener? = null

    init {
        listTimezone = emptyList()
    }

    fun setTimeZoneList(listTimezone : List<String>){
        this.listTimezone = listTimezone
    }

    interface ClickListener {
        fun onClickTimeZone(timeZone: String)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TimeZoneViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_child_timezone, viewGroup, false)
        return TimeZoneViewHolder(view)
    }

    override fun getItemCount(): Int = listTimezone.size


    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        val timeZone = listTimezone[position]
        holder.bind(timeZone)
    }

    inner class TimeZoneViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var timeZone : String

        var city : TextView = itemView.timezone_city
        var id : TextView = itemView.timezone_id

        init {
            itemView.setOnClickListener {
                clickListener?.onClickTimeZone(timeZone)
            }
        }

        fun bind(timeZone : String){
            this.timeZone = timeZone
            timeZone.indexOf('/').let {
                if(it == -1)
                    null
                else
                    city.text = timeZone.substring(it + 1)
            }
            id.text = timeZone
        }
    }
}