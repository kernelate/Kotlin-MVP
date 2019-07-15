package com.cherry.home.ui.scenario.addsmart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.ui.base.BaseActivity
import com.cherry.home.ui.scenario.automation.settings.AutoActivity
import com.cherry.home.ui.scenario.scene.settings.SSettingsActivity
import com.cherry.home.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_add_smart.*

class AddSmartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        setToolbarTitle(R.string.add_smart)

        rl_addscene.setOnClickListener {
//            val i = Intent(this, SSettingsActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
//            SSettingsActivity.gotoSceneSettingsActivityForResult(this,Constant.REQUEST_ADD_SCENE)
            val intent = Intent(this, SSettingsActivity::class.java)
            ActivityUtils.startActivityForResult(this, intent, Constant.REQUEST_ADD_SCENE, ActivityUtils.ANIMATE_FORWARD, false)
        }

        rl_auto.setOnClickListener {
            val i = Intent(this, AutoActivity::class.java)
            ActivityUtils.startActivityForResult(this, i, Constant.REQUEST_ADD_AUTO, ActivityUtils.ANIMATE_FORWARD, false)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
////        inflater.inflate(R.menu.manu, menu)
////        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.nav_add_device -> {
////            val i = Intent(this, AddDeviceActivity::class.java)
////            startActivity(i)
//            Log.d("Addsmart", " onOptionsItemSelected ")
//            true
//        }
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

    override fun snackBarLayoutId(): ViewGroup = add_smart_id

    override fun layoutId(): Int = R.layout.activity_add_smart

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
                if(resultCode == Activity.RESULT_OK){
                    Log.d("AddSmartActivity", "result ok")
                    setResult(resultCode, data)
                    finishActivity()
                }
    }

}