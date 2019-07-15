package com.cherry.home.util.rx.scheduler

import com.cherry.home.util.scheduler.BaseScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by lam on 2/6/17.
 */

class SingleMainScheduler<T> private constructor() : BaseScheduler<T>(Schedulers.single(), AndroidSchedulers.mainThread())
