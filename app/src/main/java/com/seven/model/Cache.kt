package com.seven.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cache(
//    @PrimaryKey val uid: Int,
    @PrimaryKey @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name = "response") val response: String?
)