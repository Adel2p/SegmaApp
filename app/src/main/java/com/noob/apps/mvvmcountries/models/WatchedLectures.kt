package com.noob.apps.mvvmcountries.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class WatchedLectures(
    @PrimaryKey
    var uuid: String,
    @ColumnInfo(name = "position") val position: Long
) : Serializable
