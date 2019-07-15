package com.cherry.home.ui.scenario.scenebg

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.sdk.TuyaSdk
import kotlinx.android.synthetic.main.activity_scenebg_item.view.*
import javax.inject.Inject

class SceneBackgroundAdapter @Inject constructor() : RecyclerView.Adapter<SceneBackgroundAdapter.SceneBackgroundViewHolder>() {

    private val TAG : String = "SceneBackgroundAdapter"

    private var backgroundList: List<String>

    private var clickListener: ClickListener? = null

    init {
        backgroundList = emptyList()
    }

    fun getBackground(devices: List<String>) {
        backgroundList = devices
    }

    interface ClickListener {
        fun onClickBackground(bg: String)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }


    override fun getItemCount(): Int {

        return backgroundList.size
    }

    override fun onBindViewHolder(holder: SceneBackgroundViewHolder, position: Int) {
        val bg = backgroundList[position]
        holder.bind(bg)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): SceneBackgroundViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_scenebg_item, viewGroup, false)
        return SceneBackgroundViewHolder(view)
    }

    inner class SceneBackgroundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var sceneCover: String

         var image : ImageView = itemView.iv_scenebg

        init {
            ButterKnife.bind(this, itemView)

            image?.setOnClickListener {
                clickListener?.onClickBackground(sceneCover)
            }
        }

        fun bind(bg: String) {
            sceneCover = bg
            Picasso.with(itemView.context).load(bg).into(image)
        }
    }
}