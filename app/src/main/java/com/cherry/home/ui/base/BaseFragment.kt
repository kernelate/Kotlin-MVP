package com.cherry.home.ui.base

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.util.LongSparseArray
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.cherry.home.CherryHomeApp
import com.cherry.home.R
import com.cherry.home.injection.component.ConfigPersistentComponent
import com.cherry.home.injection.component.DaggerConfigPersistentComponent
import com.cherry.home.injection.component.FragmentComponent
import com.cherry.home.injection.module.FragmentModule
import com.cherry.home.util.ConnectivityReceiver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicLong

/**
 * Abstract Fragment that every other Fragment in this application must implement. It handles
 * creation of Dagger components and makes sure that instances of ConfigPersistentComponent are kept
 * across configuration changes.
 */
abstract class BaseFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var fragmentComponent: FragmentComponent? = null
    private var fragmentId = 0L

    private var mSnackBar: Snackbar? = null

    private val mBroadcast by lazy{
        ConnectivityReceiver()
    }

    companion object {
        private val KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID"
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
        private val NEXT_ID = AtomicLong(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the FragmentComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        fragmentId = savedInstanceState?.getLong(KEY_FRAGMENT_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(fragmentId) == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", fragmentId)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(CherryHomeApp[activity as Context].component)
                    .build()
            componentsArray.put(fragmentId, configPersistentComponent)
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", fragmentId)
            configPersistentComponent = componentsArray.get(fragmentId)!!
        }
        fragmentComponent = configPersistentComponent.fragmentComponent(FragmentModule(this))
        activity?.registerReceiver(mBroadcast,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        ConnectivityReceiver.connectivityReceiverListener = this

//        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(layoutId(), container, false)
        ButterKnife.bind(this, view)
        return view
    }

    @LayoutRes abstract fun layoutId(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_FRAGMENT_ID, fragmentId)
    }

    override fun onDestroy() {
        if (!activity!!.isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", fragmentId)
            componentsArray.remove(fragmentId)
        }

        activity?.unregisterReceiver(mBroadcast)
        super.onDestroy()
    }

    fun fragmentComponent() = fragmentComponent as FragmentComponent

    fun showSnack(parent: ViewGroup, messageResId: Int, length: Int,
                  actionLabelResId: Int? = null, action: ((View) -> Unit)? = null,
                  callback: ((Snackbar) -> Unit)? = null) {

        showSnack(parent, getString(messageResId), length, actionLabelResId?.let { getString(it) }, action, callback)
    }

    fun showSnack(parent: ViewGroup, message: String, length: Int,
                  actionLabel: String? = null, action: ((View) -> Unit)? = null,
                  callback: ((Snackbar) -> Unit)? = null) {

        Snackbar.make(parent, message, length)
                .apply {
                    if (actionLabel != null) {
                        setAction(actionLabel, action)
                    }

                    callback?.invoke(this)
                }
                .show()
    }

    abstract fun snackBarLayoutId(): ViewGroup

    fun hasInternetConnection(): Single<Boolean> {
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            showSnack(snackBarLayoutId(), R.string.ty_net_useless_info, Snackbar.LENGTH_INDEFINITE, R.string.retry)
        } else {
            hasInternetConnection().subscribe { hasInternet ->
                if(hasInternet){
                    mSnackBar?.dismiss()
                } else {
                    showSnack(snackBarLayoutId(), R.string.no_internet_connect, Snackbar.LENGTH_INDEFINITE, R.string.retry)
                }
            }
        }
    }

}