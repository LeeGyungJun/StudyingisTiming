package com.augustin26.studyingistiming.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.augustin26.studyingistiming.Dday
import com.augustin26.studyingistiming.DdayContent
import com.augustin26.studyingistiming.StudyData
import com.augustin26.studyingistiming.TodayTime

@Database(entities = [Dday::class, DdayContent::class, TodayTime::class, StudyData::class], version = 11, exportSchema = false)
abstract class StudyDatabase: RoomDatabase() {
    abstract fun studyDAO() : StudyDAO

    /*
    companion object {
        var INSTANCE : StudyDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : StudyDatabase? {
            if (INSTANCE == null) {
                synchronized(StudyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    StudyDatabase::class.java, "study.db")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

    }
     */
}