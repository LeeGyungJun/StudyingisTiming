package com.augustin26.studyingistiming.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.receiver.AlarmReceiver
import com.augustin26.studyingistiming.ui.CustomB
import com.augustin26.studyingistiming.ui.MainActivity
import java.util.*
import kotlin.properties.Delegates


class Foreground : Service() {

    val TAG = "Foreground"

    var timer : CountUpTimer? = null
    var time by Delegates.notNull<Long>()

    private val PREFS_NAME = "_pref_name"
    private val CURRENT_TIME = "_current_time"
    private var editor: SharedPreferences.Editor? = null
    private var sharedPreference: SharedPreferences? = null

    val CHANNEL_ID = "FGS153"
    val NOTI_ID = 153
    var dontDie = false

    //Room 변수
    var helper : StudyDatabase? = null

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID,
                "FOREGROUND", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //RoomDB 빌드
        helper = Room.databaseBuilder(this, StudyDatabase::class.java, "study_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        if (intent?.action == CustomB.Actions.START_FOREGROUND) {
            startForegroundService(helper!!)
        }else if (intent?.action == CustomB.Actions.STOP_FOREGROUND) {
            dontDie = false
            stopForegroundService()
        }
        helper!!.close()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    private fun startForegroundService(helper: StudyDatabase) {
        createNotificationChannel()
        dontDie = true
        sharedPreference = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val start = System.currentTimeMillis()
        val cur = if (getCurrentTime() != 0.toLong()) getCurrentTime() else 0

        // 타이머
        timer = object : CountUpTimer(864000) {

            // 똑딱똑딱
            override fun onTick(second: Int) {

                time = cur + ((System.currentTimeMillis() - start) / 1000)
                setCurrentTime(time)

                val notiTitle = when (time % 4.toLong()) {
                    1.toLong()-> "공부 중"
                    2.toLong()-> "공부 중."
                    3.toLong()-> "공부 중.."
                    else-> "공부 중..."
                }

                //앱 중복 실행 방지
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val pIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


                //알림 레이어 설정
                val layout = RemoteViews(packageName, R.layout.custom_notification)
                layout.setTextViewText(R.id.notifyTitle, notiTitle)
                layout.setTextViewText(R.id.notifyMessage, formatTime(time))

                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setCustomContentView(layout)
                    .build()

                startForeground(NOTI_ID, notification)
                Log.d("서비스","$time")
            }

            // 타이머 종료
            override fun onFinish() {
                super.onFinish()

                Log.e("Foreground","onFinish")
                if (dontDie) {
                    setCurrentTime(time + 1) //시간 오차 조정 (1초)
                    setAlarmTimer()
                    Thread.currentThread().interrupt()
                }
            }
        }
        timer!!.start()
    }

    private fun stopForegroundService() {
        timer!!.cancel()
        stopForeground(true)
        stopSelf()
    }

    //시간 포맷 함수
    fun formatTime(time: Long) : String {
        val hour = String.format("%02d", time/(60*60))
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "$hour:$minute:$second"
    }

    fun setCurrentTime(time : Long) {
        editor = sharedPreference!!.edit()
        editor!!.putLong(CURRENT_TIME, time)!!.apply()
    }

    fun getCurrentTime(): Long {
        return sharedPreference!!.getLong(CURRENT_TIME, 0)
    }

    fun setAlarmTimer() {
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        c.add(Calendar.SECOND, 1)
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        val mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, sender)
        Log.e("Foreground","setAlarmTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG,"onDestroy")
        if (dontDie) {
            setAlarmTimer()
            Thread.currentThread().interrupt()
//            if (mainThread != null) {
//                mainThread!!.interrupt();
//                mainThread = null;
//            }
        }
    }

    abstract class CountUpTimer protected constructor(private val duration: Long) :
        CountDownTimer(duration, INTERVAL_MS) {
        abstract fun onTick(second: Int)
        override fun onTick(msUntilFinished: Long) {
            val second = ((duration - msUntilFinished) / 1000).toInt()
            onTick(second)
        }

        override fun onFinish() {
            onTick(duration / 1000)
        }

        companion object {
            private const val INTERVAL_MS: Long = 1000
        }
    }
}
