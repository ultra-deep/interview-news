package com.seven.util.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.seven.model.Cache

@Dao
interface CachceDao{

//    @Query("SELECT * FROM cache WHERE uid IN (:id)")
//    override fun get(id: IntArray): List<Cache>

    @Query("SELECT * FROM cache WHERE url LIKE :url")
    fun get(url:String): Cache

    @Insert
    fun cache(cache: Cache)

    @Delete
    fun delete(cache: Cache)
}