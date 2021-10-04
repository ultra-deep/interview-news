package com.seven.util.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seven.model.Cache

@Database(entities = arrayOf(Cache::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacherDao(): CachceDao
}