package com.cherry.home.ui.scenario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseFragment
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.ui.main.MainMvpView
import com.cherry.home.ui.scenario.addsmart.AddSmartActivity
import com.cherry.home.ui.scenario.automation.settings.AutoActivity
import com.cherry.home.ui.scenario.scene.settings.SSettingsActivity
import com.cherry.home.util.NetworkUtil
import com.cherry.home.util.gone
import com.cherry.home.util.visible
import com.tuya.smart.home.sdk.bean.scene.SceneBean
import com.tuya.smart.home.sdk.bean.scene.SceneCondition
import kotlinx.android.synthetic.main.fragment_scenario.*
import javax.inject.Inject

class ScenarioFragment : BaseFragment(), ScenarioView, ScenarioAdapter.ClickListener, AutomationAdapter.ClickListener {


    @Inject lateinit var scenarioPresenter: ScenarioPresenter
    @Inject lateinit var scenarioAdapter: ScenarioAdapter

    @Inject lateinit var automationAdapter: AutomationAdapter

    companion object {
        val TAG : String = "ScenarioFragment"
    }


    fun newInstance(): ScenarioFragment {
        return ScenarioFragment()
    }

    override fun layoutId(): Int = R.layout.fragment_scenario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        scenarioPresenter.attachView(this)

        activity?.apply {
            title = getString(R.string.smart)
            setHasOptionsMenu(true)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, " onactivity created ")

        recyclerView_scene?.apply {
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(GridItemDecoration(10,2))
            adapter = scenarioAdapter
        }

        recyclerView_auto?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = automationAdapter
        }

        swipeToRefresh?.apply {
            setProgressBackgroundColorSchemeResource(R.color.primary)
            setColorSchemeResources(R.color.white)
            setOnRefreshListener {
                if (NetworkUtil.isNetworkConnected(context)) {
                    scenarioPresenter.getSmartList()
                } else {
                    swipeToRefresh?.isRefreshing = false
                    swipeToRefresh?.gone()
                    showSnack(parent_layout, R.string.ty_net_useless_info, Snackbar.LENGTH_INDEFINITE, R.string.retry)
                }
            }
        }

        scenarioAdapter.setClickListener(this)
        automationAdapter.setClickListener(this)

        onClick()
        (activity as MainMvpView).showHamburgerIcon()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_add_smart, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add -> {
                val intent = Intent(activity, AddSmartActivity::class.java)
                startActivityForResult(intent, Constant.REQUEST_ADD_SCENE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        scenarioPresenter.getSmartList()

        hasInternetConnection().subscribe{ hasInternet->
            Log.d(TAG, "onResume() : hasInternet: $hasInternet")
            if(hasInternet){
                Log.d(TAG, "onResume() : hasInternet")

            }
        }
    }

    override fun showProgress(show: Boolean) {
        if (show) {
            if (recyclerView_scene?.visibility == View.VISIBLE && recyclerView_auto?.visibility == View.VISIBLE ) {
                swipeToRefresh?.isRefreshing = true
            } else {
                if(scenarioAdapter.itemCount ==0 && automationAdapter.itemCount == 0){
                    progressBar?.visible()
                    recyclerView_scene?.gone()
                    recyclerView_auto?.gone()
                    swipeToRefresh?.gone()
                } else {
                    swipeToRefresh?.isRefreshing = true
                }

            }
        } else {
            swipeToRefresh?.isRefreshing = false
            progressBar?.gone()
            recyclerView_scene?.visible()
            recyclerView_auto?.visible()
        }
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        showSnack(parent_layout,error, Snackbar.LENGTH_LONG, null )
    }

    override fun showNoSceneLayout(show: Boolean) {
        if(show){
            scene_list_rlayout.gone()
            no_scene_layout.visible()
        } else {
            no_scene_layout.gone()
            scene_list_rlayout.visible()
        }
    }

    override fun getScenario(scenario: ArrayList<SceneBean>) {
        scenarioAdapter?.apply {
            displayScenario(scenario)
            notifyDataSetChanged()
        }
    }

    override fun getCondition(condition: List<SceneBean>) {
        automationAdapter?.apply {
            displayScenario(condition)
            notifyDataSetChanged()
        }
    }

    override fun openScenario(scenario: SceneBean) {
        val intent = Intent(activity, SSettingsActivity::class.java)
        intent.putExtra("scene", scenario)
        intent.putExtra("requestCode",Constant.REQUEST_MODIFY_SCENETASK )
        startActivityForResult(intent, Constant.REQUEST_MODIFY_SCENETASK)
//        SSettingsActivity.gotoSceneSettingsActivityForResult(activity!!, scenario, Constant.REQUEST_MODIFY_SCENE)
    }

    override fun runScenario(scenario: SceneBean){
        scenarioPresenter.executeScene(scenario)
    }

    override fun onClickAutomation(sceneBean: SceneBean) {
        val intent = Intent(activity, AutoActivity::class.java)
        intent.putExtra("scene", sceneBean)
        intent.putExtra("requestCode",Constant.REQUEST_MODIFY_AUTO )
        startActivityForResult(intent, Constant.REQUEST_MODIFY_AUTO)
    }

    override fun setEnable(sceneBean: SceneBean) {
        scenarioPresenter.setAutomationEnable(sceneBean)
    }

    override fun gotoSceneSettings(scenario: SceneBean) {
        openScenario(scenario)
    }

    override fun showToast() {
        Toast.makeText(activity, R.string.offline_devices, Toast.LENGTH_LONG).show()
    }

    override fun snackBarLayoutId(): ViewGroup = parent_layout

    private fun onClick(){
        scene_add_scene.setOnClickListener {
            val intent = Intent(activity, AddSmartActivity::class.java)
            startActivityForResult(intent, Constant.REQUEST_ADD_SCENE)
        }

        ll_scene.setOnClickListener {
            if (recyclerView_scene.visibility == View.VISIBLE){
                recyclerView_scene.gone()
                iv_scene.setImageResource(R.drawable.ic_right_arrow)
            }
            else {
                recyclerView_scene.visible()
                iv_scene.setImageResource(R.drawable.ic_down_arrow)
            }
        }

        ll_automation.setOnClickListener {
            if (recyclerView_auto.visibility == View.VISIBLE) {
                recyclerView_auto.gone()
                iv_automation.setImageResource(R.drawable.ic_right_arrow)
            }else{
                recyclerView_auto.visible()
                iv_automation.setImageResource(R.drawable.ic_down_arrow)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        scenarioPresenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        scenarioPresenter.onDestroy()
        scenarioPresenter.detachView()
    }

    override fun showScene(){
        if (scenarioAdapter.itemCount == 0){
            recyclerView_scene.gone()
            iv_scene.setImageResource(R.drawable.ic_right_arrow)
        }
        else {
            recyclerView_scene.visible()
            iv_scene.setImageResource(R.drawable.ic_down_arrow)
        }
    }

     override fun showAutomation(){
        if (recyclerView_auto.visibility == View.VISIBLE && automationAdapter.itemCount == 0) {
            recyclerView_auto.gone()
            iv_automation.setImageResource(R.drawable.ic_right_arrow)
        }else{
            recyclerView_auto.visible()
            iv_automation.setImageResource(R.drawable.ic_down_arrow)
        }
    }
}