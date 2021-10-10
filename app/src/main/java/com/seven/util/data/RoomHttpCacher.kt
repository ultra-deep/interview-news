package com.seven.util.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seven.model.Cache

/**
 * @author Richi on 10/4/21.
 */
class RoomHttpCacher : HttpCacher {


    lateinit var dao : CachceDao
    constructor(applicationContext :Context)
    {
        var db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-name").allowMainThreadQueries().build()
        dao = db. cacherDao()
    }


    override fun cache(cache: Cache) {
        dao.cache(cache)
    }

    override fun get(url: String): Cache? {
        return dao.get(url)
    }
}