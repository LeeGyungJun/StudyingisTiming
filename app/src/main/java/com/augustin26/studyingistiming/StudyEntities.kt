package com.augustin26.studyingistiming

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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

@Entity(tableName = "today_time")
data class TodayTime (
    @PrimaryKey
    @ColumnInfo
    var id: Long? = null,
    @ColumnInfo
    var time: Int? = null
)

@Entity(tableName = "study_data")
data class StudyData (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Long? = null,
    @ColumnInfo(name = "date")
    var date_info : String? = null,
    @ColumnInfo
    var time: Int? = null
)