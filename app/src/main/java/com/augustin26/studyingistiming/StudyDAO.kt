package com.augustin26.studyingistiming

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface StudyDAO {

    @Query("select * from dday")
    fun getDay() : List<StudyEntity>

    @Insert(onConflict = REPLACE)
    fun insertDday(dday: StudyEntity)

    @Delete
    fun deleteDay(dday: StudyEntity)
}