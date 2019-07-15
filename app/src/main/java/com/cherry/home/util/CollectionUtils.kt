package com.cherry.home.util

object CollectionUtils {

    fun isEmpty(collection: Collection<*>?): Boolean {
        return null == collection || collection.isEmpty()
    }

    fun isNotEmpty(collection: Collection<*>?): Boolean {
        return null != collection && !collection.isEmpty()
    }

}
