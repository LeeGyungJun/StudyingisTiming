package com.augustin26.studyingistiming.receiver

import android.app.ActivityManager
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.augustin26.studyingistiming.Const
import com.augustin26.studyingistiming.StudyData
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.TodayTime
import com.augustin26.studyingistiming.ui.CustomB
import com.augustin26.studyingistiming.ui.MainActivity
import com.augustin26.studyingistiming.util.Utils
import java.text.SimpleDateFormat
import java.util.*

class StudyReceiver : BroadcastReceiver() {

    //Room 변수
    private var helper : StudyDatabase? = null

    private val now: Long = System.currentTimeMillis()
    private val date = Date(now)
    private val dateFormatYear = SimpleDateFormat("yyyy", Locale("ko", "KR"))
    private val dateFormatMonth = SimpleDateFormat("M", Locale("ko", "KR"))
    private val dateFormatDay = SimpleDateFormat("d", Locale("ko", "KR"))

    private val year = dateFormatYear.format(date).toInt()
    private val month = dateFormatMonth.format(date).toInt()
    private val day = dateFormatDay.format(date).toInt()

    override fun onReceive(context: Context, intent: Intent) {

        //RoomDB 빌드
        helper = Room.databaseBuilder(context, StudyDatabase::class.java, "study_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        val action = intent.action
        Log.e("broad","action : $action")

        if (action.equals(Intent.ACTION_DATE_CHANGED)) {
            val time = helper?.studyDAO()?.getTime()?.get(0)?.time
            val data = StudyData(null, year, month, day, time)
            helper?.studyDAO()?.insertStudy(data)
            helper?.studyDAO()?.insertTime(TodayTime(1,0))
            //Toast.makeText(context, "오늘 공부한 시간을 저장합니다.", Toast.LENGTH_SHORT).show()
            Log.e("broad","data : ${helper?.studyDAO()?.getStudy()}")
            Log.e("broad","time : ${helper?.studyDAO()?.getTime()}")

            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val procInfos = activityManager.runningAppProcesses
            if (procInfos != null) {
                for (processInfo in procInfos) {
                    if (processInfo.processName == context.packageName) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra(Const.Receiver.DATE_CHANGE, true)
                        startActivity(context, intent, null)
                    }
                }
            }
        }
        helper!!.close()
    }
}