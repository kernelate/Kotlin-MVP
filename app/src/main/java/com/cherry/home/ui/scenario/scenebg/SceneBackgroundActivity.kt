package com.cherry.home.ui.scenario.scenebg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.device.view.GridItemDecoration
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_scenebg.*
import javax.inject.Inject

class SceneBackgroundActivity : BaseActivity(), SceneBackgroundView, SceneBackgroundAdapter.ClickListener {

    private val TAG : String = "SceneBackgroundActivity"

    @Inject lateinit var sceneBackgroundPresenter: SceneBackgroundPresenter
    @Inject lateinit var sceneBackgroundAdapter: SceneBackgroundAdapter

    companion object {
        fun gotoSceneBackgroundForResult(mContext: Activity, requestCode: Int) {
            val intent = Intent(mContext, SceneBackgroundActivity::class.java)
            ActivityUtils.startActivityForResult(mContext, intent, requestCode, 0, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        sceneBackgroundPresenter.attachView(this)

        initToolbar()
        setToolbarTitle(R.string.scene_cover)

        sceneBackgroundPresenter.getSceneBackgroundList()

        recyclerView.apply {
            layoutManager = GridLayoutManager(context,2)
            addItemDecoration(GridItemDecoration(20,2))
            adapter = sceneBackgroundAdapter
        }

        sceneBackgroundAdapter.setClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        sceneBackgroundPresenter.detachView()
    }

    override fun layoutId() = R.layout.activity_scenebg
    override fun snackBarLayoutId(): ViewGroup = scenebg_layout

    override fun showError(error_msg: String) {
        showSnack(snackBarLayoutId(), error_msg, Snackbar.LENGTH_LONG, null)
    }

    override fun updateImageView(bgList: ArrayList<String>) {
        if(!bgList.isEmpty()){
            sceneBackgroundAdapter?.apply {
                getBackground(bgList)
                notifyDataSetChanged()
            }
        }
    }

    override fun onClickBackground(bg: String) {
        val result = Intent()
        result.putExtra("image", bg)
        setResult(Activity.RESULT_OK, result)
        finishActivity()
    }
}