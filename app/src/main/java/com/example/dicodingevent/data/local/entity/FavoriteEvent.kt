package com.example.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "FavoriteEvent")
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "mediaCover")
    var mediaCover: String? = null
)