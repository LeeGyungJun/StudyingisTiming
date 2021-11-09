package com.augustin26.studyingistiming.receiver

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.room.Room
import com.augustin26.studyingistiming.util.Const
import com.augustin26.studyingistiming.StudyData
import com.augustin26.studyingistiming.TodayTime
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*

//DATE_CHANGED 날짜 변경 시 공부한 시간을 저장하는 리시버

class StudyReceiver : BroadcastReceiver() {

    //Room 변수
    private var helper : StudyDatabase? = null

    private val now: Long = System.currentTimeMillis()-10000 // 몇초의 오차때문에 다음날로 저장할 경우를 대비하여 10초 전으로 맞춤 (23시 59분 50초)
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

            //오늘 날짜 시간 데이터 저장
            helper?.studyDAO()?.insertStudy(data)
            helper?.studyDAO()?.insertTime(TodayTime(1,0))

            //Toast.makeText(context, "오늘 공부한 시간을 저장합니다.", Toast.LENGTH_SHORT).show()
            helper?.studyDAO()?.getStudy()?.forEach {
                Log.e("broad","data : ${it}")
            }
            Log.e("broad","time : ${helper?.studyDAO()?.getTime()}")
            Log.e("broad", "year : $year\nmonth : $month\nday : $day")

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

    fun isServiceRunningCheck(context: Context) : Boolean {
        val manager: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.augustin26.studyingistiming.service.StudyForeground" == service.service.className) {
                return true
            }
        }
        return false
    }
}