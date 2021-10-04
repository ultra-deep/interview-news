package com.seven.util.data

import com.seven.model.Cache

/**
 * @author Richi on 10/4/21.
 */
interface HttpCacher {
    fun cache(cache: Cache)
    fun get(url:String) : Cache?
}