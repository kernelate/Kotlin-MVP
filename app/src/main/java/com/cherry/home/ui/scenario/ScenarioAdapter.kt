package com.cherry.home.ui.scenario

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
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import kotlinx.android.synthetic.main.activity_scenario_item.view.*
import javax.inject.Inject

class ScenarioAdapter @Inject constructor() : RecyclerView.Adapter<ScenarioAdapter.ScenearioViewHolder>(){

    private val TAG : String = "SceneBackgroundAdapter"

    private var scenarioList: List<SceneBean>

    private var clickListener: ClickListener? = null

    init {
        scenarioList = emptyList()
    }

    fun displayScenario(devices: List<SceneBean>) {
        scenarioList = devices
    }

    interface ClickListener {
        fun openScenario(scenario: SceneBean)
        fun runScenario(scenario: SceneBean)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ScenearioViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_scenario_item, viewGroup, false)
        return ScenearioViewHolder(view)
    }

    override fun getItemCount(): Int = scenarioList.size

    override fun onBindViewHolder(holder: ScenearioViewHolder, position: Int) {
        val scenario = scenarioList[position]
        holder.bind(scenario)
    }

    inner class ScenearioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var sceneBean: SceneBean

         var rl_bg : RelativeLayout = itemView.rl_editname_img

         var sceneName : TextView = itemView.scene_name

         var scene_dots : ImageView = itemView.scene_dots

         var image: ImageView = itemView.iv_scenario

        init {
            ButterKnife.bind(this, itemView)
            scene_dots?.setOnClickListener {
                clickListener?.openScenario(sceneBean)
            }

            rl_bg?.setOnClickListener {
                clickListener?.runScenario(sceneBean)
            }
        }

        fun bind(scenario: SceneBean) {
            sceneBean = scenario
            sceneName?.text = scenario.name
                Picasso.with(itemView.context).load(scenario.background).into(image)
        }

    }
}