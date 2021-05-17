package com.augustin26.studyingistiming

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dday_day")
data class Dday (
    @PrimaryKey
    @ColumnInfo
    var id: Long? = null,
    @ColumnInfo
    var day: Long? = null
)

@Entity(tableName = "dday_content")
data class DdayContent (
    @PrimaryKey
    @ColumnInfo
    var id: Long? = null,
    @ColumnInfo
    var content: String? = null
)