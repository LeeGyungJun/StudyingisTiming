package com.augustin26.studyingistiming

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "studydb", null, 1) {

    private val ddaySQL = "create table if not exists dday " +
            "(id integer primary key autoincrement," +
            "day int)"
    private val ddaycontentSQL = "create table if not exists ddaycontent " +
            "(id integer primary key autoincrement," +
            "content text)"


    override fun onCreate(db: SQLiteDatabase) {
        db?.execSQL(ddaySQL)
        db?.execSQL(ddaycontentSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists dday")
        db?.execSQL("drop table if exists ddaycontent")
        onCreate(db)
    }

    fun insertDday(day: Int) {
        deleteDday()
        val values = ContentValues()
        values.put("day", day)

        val wd = writableDatabase
        wd.insert("dday", null, values)
        wd.close()
    }
    fun insertDdayContent(content: String) {
        deleteDdayContent()
        val values = ContentValues()
        values.put("content", content)

        val wd = writableDatabase
        wd.insert("ddaycontent", null, values)
        wd.close()
    }

    fun selectDday(): Int? {
        var result: Int? = null

        val select = "select * from dday"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)

        while (cursor.moveToNext()) {
            val day = cursor.getInt(cursor.getColumnIndex("day"))

            result = day
        }
        cursor.close()
        rd.close()

        return result
    }

    fun selectDdayContent(): String? {
        var result: String? = null

        val select = "select * from ddaycontent"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)

        while (cursor.moveToNext()) {
            val content = cursor.getString(cursor.getColumnIndex("content"))

            result = content
        }
        cursor.close()
        rd.close()

        return result
    }

    fun deleteDday() {
        val delete = "delete from dday"
        val db = writableDatabase

        db.execSQL(delete)
        db.close()
    }
    fun deleteDdayContent() {
        val delete = "delete from ddaycontent"
        val db = writableDatabase

        db.execSQL(delete)
        db.close()
    }
}