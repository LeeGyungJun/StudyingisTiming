package com.augustin26.studyingistiming

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface StudyDAO {

    //디데이
    @Query("select * from dday_day")
    fun getDay() : List<Dday>

    //디데이 내용
    @Query("select * from dday_content")
    fun getContent() : List<DdayContent>

    @Insert(onConflict = REPLACE)
    fun insertDay(day: Dday)

    @Insert(onConflict = REPLACE)
    fun insertContent(content: DdayContent)

    @Delete
    fun deleteDay(day: Dday)

    @Delete
    fun deleteContent(content: DdayContent)
}