package com.cherry.home.ui.base

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.tuya.smart.android.common.utils.SafeHandler
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
open class BasePresenter<T : MvpView> : Presenter<T>, Handler.Callback {


    var mvpView: T? = null
        private set

    private val compositeSubscription = CompositeSubscription()

    protected var mHandler: SafeHandler? = null


    override fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        mvpView = null
        if (!compositeSubscription.isUnsubscribed) {
            compositeSubscription.clear()
        }
    }

    private val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(subs: Subscription) {
        compositeSubscription.add(subs)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")

    fun getHandler() {
        this.mHandler = SafeHandler(Looper.getMainLooper(), this)
    }

    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    open fun onDestroy(){
        this.mHandler?.destroy()
    }

}

