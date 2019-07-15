package com.cherry.home.ui.scenario

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.cherry.home.R
import com.squareup.picasso.Picasso
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import kotlinx.android.synthetic.main.activity_automation_item.view.*
import javax.inject.Inject

class AutomationAdapter @Inject constructor() : RecyclerView.Adapter<AutomationAdapter.AutomationViewHolder>() {

    private val TAG : String = "AutomationAdapter"

    private var conditionList: List<SceneBean>

    private var clickListener: ClickListener? = null

    init {
        conditionList = emptyList()
    }

    fun displayScenario(devices: List<SceneBean>) {
        conditionList = devices
    }

    interface ClickListener {
        fun onClickAutomation(sceneBean: SceneBean)
        fun setEnable(sceneBean: SceneBean)
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AutomationViewHolder {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.activity_automation_item, viewGroup, false)

        return AutomationViewHolder(view)
    }

    override fun getItemCount(): Int = conditionList.size

    override fun onBindViewHolder(p0: AutomationViewHolder, p1: Int) {
        val condition = conditionList[p1]
        p0.bind(condition)
    }

    inner class AutomationViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        private lateinit var conditionBean: SceneBean

         var iv_background : ImageView = itemView.iv_automation_bg

         var tv_automationName : TextView = itemView.tv_automation_name

        var automationSwitch : Switch = itemView.switch_auto



        init {
            ButterKnife.bind(this, itemView)
            automationSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
                Log.d(TAG, "isChecked $isChecked compoundButton ${compoundButton.isPressed}")
                if(compoundButton.isPressed){
                    conditionBean.isEnabled = isChecked
                    clickListener?.setEnable(conditionBean)
                }
            }
            itemView.setOnClickListener {
                clickListener?.onClickAutomation(conditionBean)
            }
        }

        fun bind(condition: SceneBean) {
            conditionBean = condition
            tv_automationName?.text = condition.name
            Picasso.with(itemView.context).load(condition.background).into(iv_background)

            automationSwitch.isChecked = condition.isEnabled

            Log.d(TAG,"id ${condition.id}")
        }
    }
}