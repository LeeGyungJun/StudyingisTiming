package com.augustin26.studyingistiming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import kotlinx.android.synthetic.main.layout_a.view.*
import java.text.SimpleDateFormat
import java.util.*

class StudyReceiver : BroadcastReceiver() {

    //Room 변수
    private var helper : StudyDatabase? = null

    private val now: Long = System.currentTimeMillis()
    private val date = Date(now)
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale("ko", "KR"))

    private val nowDate = dateFormat.format(date).toString()

    override fun onReceive(context: Context, intent: Intent) {

        //RoomDB 빌드
        helper = Room.databaseBuilder(context, StudyDatabase::class.java, "study_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        var action = intent.action
        Log.e("broad","action : $action")

        if (action.equals(Intent.ACTION_DATE_CHANGED)) {
            val time = helper?.studyDAO()?.getTime()?.get(0)?.time
            val data = StudyData(null, nowDate, time)
            helper?.studyDAO()?.insertStudy(data)
            helper?.studyDAO()?.insertTime(TodayTime(1,0))
            Toast.makeText(context, "오늘 공부한 시간을 저장합니다.", Toast.LENGTH_SHORT).show()
            Log.e("broad","data : ${helper?.studyDAO()?.getStudy()}")
            Log.e("broad","time : ${helper?.studyDAO()?.getTime()}")
        }
    }
}