package com.cherry.home.ui.base

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.os.SystemClock
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.util.LongSparseArray
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import butterknife.ButterKnife
import com.cherry.home.BuildConfig
import com.cherry.home.CherryHomeApp
import com.cherry.home.R
import com.cherry.home.app.Constant
import com.cherry.home.injection.component.ActivityComponent
import com.cherry.home.injection.component.ConfigPersistentComponent
import com.cherry.home.injection.component.DaggerConfigPersistentComponent
import com.cherry.home.injection.module.ActivityModule
import com.cherry.home.util.*
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.home.sdk.TuyaHomeSdk
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_top.*
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract activity that every other Activity in this application must implement. It provides the
 * following functionality:
 * - Handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent are kept across configuration changes.
 * - Set up and handles a GoogleApiClient instance that can be used to access the Google sign in
 * api.
 * - Handles signing out when an authentication error event is received.
 */
abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener  {

    private val TAG = "BaseActivity"

    private var activityComponent: ActivityComponent? = null
    private var activityId = 0L

    private var mIsPaused = true

    protected var mPanelTopView: View? = null

    private var resumeUptime: Long = 0

    private var mGestureDetector: GestureDetector? = null

    private var mNeedDefaultAni : Boolean = true

    protected var mToolBar: Toolbar? = null

    private var isExit : Boolean = false

    private var mSnackBar: Snackbar? = null

    private val mBroadcast by lazy{
        ConnectivityReceiver()
    }

    companion object {
        private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(layoutId())
        ButterKnife.bind(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val gestureListener = obtainGestureListener()
        if (gestureListener != null) {
            mGestureDetector = GestureDetector(this, gestureListener)
        }
        Constant.attachActivity(this)
        checkLogin()
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectAll()
                .penaltyLog()
                .build())
        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        activityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(activityId) == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(CherryHomeApp[this].component)
                    .build()
            componentsArray.put(activityId, configPersistentComponent)
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = componentsArray.get(activityId)!!
        }
        activityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
        activityComponent?.inject(this)

    }

    @LayoutRes abstract fun layoutId(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, activityId)
    }

    fun closeDefaultAni() {
        mNeedDefaultAni = false
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", activityId)
            componentsArray.remove(activityId)
        }

        unregisterReceiver(mBroadcast)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun activityComponent() = activityComponent as ActivityComponent

    private fun checkLogin(){
        if(needLogin() && !TuyaHomeSdk.getUserInstance().isLogin()){
            LoginHelper.reLogin(this)
        }
    }

    open fun needLogin(): Boolean {
            return false
    }

    private fun obtainGestureListener(): GestureDetector.OnGestureListener? {
        return null
    }

    protected fun initToolbar() {
        setSupportActionBar(main_toolbar)
        main_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getToolBar(): Toolbar {
        return mToolBar!!
    }

    protected fun setToolbarTitle(title: Int) {
        if (main_toolbar != null) {
            supportActionBar?.apply {
                setTitle(title)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }


    protected fun setSubTitle(title: String) {
        if (mToolBar != null) {
            mToolBar?.setSubtitle(title)
        }
    }

    protected fun setLogo(logo: Drawable) {
        if (mToolBar != null) {
            mToolBar?.setLogo(logo)
        }
    }

    protected fun setNavigationIcon(logo: Drawable) {
        if (mToolBar != null) {
            mToolBar?.setNavigationIcon(logo)
        }
    }

    protected fun setMenu(resId: Int, listener: Toolbar.OnMenuItemClickListener) {
        if (mToolBar != null) {
            mToolBar?.inflateMenu(resId)
            mToolBar?.setOnMenuItemClickListener(listener)
        }
    }

    protected fun setDisplayHomeAsUpEnabled(iconResId: Int, listener: View.OnClickListener?) {
        if (mToolBar != null) {
            mToolBar?.setNavigationIcon(iconResId)
            if (listener != null) {
                mToolBar?.setNavigationOnClickListener(listener)
            } else {
                mToolBar?.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })
            }
        }
    }

    protected fun hideToolBarView() {
        if (mToolBar != null) {
            mToolBar?.setVisibility(View.GONE)
        }
    }

    protected fun showToolBarView() {
        if (mToolBar != null) {
            mToolBar?.setVisibility(View.VISIBLE)
        }
    }

    @TargetApi(19)
    protected fun setTranslucentStatus(on: Boolean) {
        val win = window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onResume() {
        super.onResume()
        mIsPaused = false
        resumeUptime = SystemClock.uptimeMillis()

        registerReceiver(mBroadcast,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onPause() {
        super.onPause()
        mIsPaused = true
    }

    override fun onBackPressed() {
        ActivityUtils.back(this)
        super.onBackPressed()
        if (mNeedDefaultAni) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    protected fun isPause(): Boolean {
        return mIsPaused
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mGestureDetector != null) {
            mGestureDetector?.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (!this.isFinishing) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                val eventtime = event.eventTime
                if (Math.abs(eventtime - resumeUptime) < 400) {
                    return true
                }
            }

            if (event.repeatCount <= 0 && !onPanelKeyDown(keyCode, event)) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ActivityUtils.back(this)
                    return true
                } else {
                    return super.onKeyDown(keyCode, event)
                }
            } else {
                return true
            }

        } else {
            return true
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            true
        } else super.onKeyUp(keyCode, event)
    }

    open fun onPanelKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            return true
        }
        return false
    }

    protected fun exitConfirmation(){
        var tExit: Timer?
        if (!isExit) {
            isExit = true
            ToastUtil.shortToast(this, getString(R.string.action_tips_exit_hint) + " "
                    + getString(R.string.app_name))
            tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }, 2000)
        } else {
            LoginHelper.exit(this)
        }
    }

    fun initSystemBarColor() {
//        CommonUtil.initSystemBarColor(this) //TODO
    }

    protected fun hideIMM() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    protected fun setViewGone(view: View) {
        if (view.visibility != View.GONE) {
            view.visibility = View.GONE
        }
    }

    fun setViewVisible(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
    }

    fun finishActivity() {
        finish()
    }

    fun showSnack(parent: ViewGroup, messageResId: Int, length: Int,
                  actionLabelResId: Int? = null, action: ((View) -> Unit)? = null,
                  callback: ((Snackbar) -> Unit)? = null) {

        showSnack(parent, getString(messageResId), length, actionLabelResId?.let { getString(it) }, action, callback)
    }

    fun showSnack(parent: ViewGroup, message: String, length: Int,
                          actionLabel: String? = null, action: ((View) -> Unit)? = null,
                          callback: ((Snackbar) -> Unit)? = null) {

        mSnackBar = Snackbar.make(parent, message, length)
        mSnackBar?.apply {
            if (actionLabel != null) {
                setAction(actionLabel, action)
            }
            callback?.invoke(this)
        }
        mSnackBar?.show()
    }

    private fun hasInternetConnection(): Single<Boolean> {
        return Single.fromCallable {
            try {
                // Connect to Google DNS to check for connection
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                socket.connect(socketAddress, timeoutMs)
                socket.close()

                true
            } catch (e: IOException) {
                false
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    abstract fun snackBarLayoutId(): ViewGroup

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            showSnack(snackBarLayoutId(), R.string.ty_net_useless_info, Snackbar.LENGTH_INDEFINITE, R.string.retry)
        } else {
            hasInternetConnection().subscribe { hasInternet ->
                Log.d("Scenario", " hasInternet $hasInternet ")
                if(hasInternet){
                    mSnackBar?.dismiss()
                } else {
                    showSnack(snackBarLayoutId(), R.string.no_internet_connect, Snackbar.LENGTH_INDEFINITE, R.string.retry)
                }
            }
        }
    }

}