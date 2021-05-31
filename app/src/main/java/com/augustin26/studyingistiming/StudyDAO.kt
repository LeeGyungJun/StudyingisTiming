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

    @Insert(onConflict = REPLACE)
    fun insertDay(day: Dday)



    //디데이 내용
    @Query("select * from dday_content")
    fun getContent() : List<DdayContent>

    @Insert(onConflict = REPLACE)
    fun insertContent(content: DdayContent)



    //오늘 공부한 시간
    @Query("select * from today_time")
    fun getTime() : List<TodayTime>

    @Insert(onConflict = REPLACE)
    fun insertTime(time: TodayTime)



    //일별 공부 데이터
    @Query("select * from study_data")
    fun getStudy() : List<StudyData>

    @Query("select * from study_data where year = :year and month = :month")
    fun getStudyDate(year: Int, month: Int) : List<StudyData>

    @Insert(onConflict = REPLACE)
    fun insertStudy(data: StudyData)

    @Delete
    fun deleteStudy(data: StudyData)
    
}