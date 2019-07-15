package com.cherry.home.util

import java.util.concurrent.CountDownLatch


/**
 * Constructs a `CountDownLatch` initialized with the given count.
 *
 * @param count the number of times [.countDown] must be invoked
 * before threads can pass through [.await]
 * @throws IllegalArgumentException if `count` is negative
 */
data class DpCountDownLatch(val count: Int) : CountDownLatch(count) {
    var status: Int = 0
    var isFromCloud: Boolean = false
    var returnValue: String? = null
    var sendDpId: String? = null

    companion object {
        val STATUS_ERROR = 1
        val STATUS_SUCCESS = 2
        val STATUS_SEND_ERROR = 3
    }
}