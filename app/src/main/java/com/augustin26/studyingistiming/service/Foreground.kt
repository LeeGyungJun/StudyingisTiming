package com.augustin26.studyingistiming.service

import android.app.*
import android.app.PendingIntent.FLAG_ONE_SHOT
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
import com.augustin26.studyingistiming.TodayTime
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.receiver.AlarmReceiver
import com.augustin26.studyingistiming.receiver.MyReceiver
import com.augustin26.studyingistiming.ui.CustomB
import com.augustin26.studyingistiming.ui.MainActivity
import java.util.*
import kotlin.properties.Delegates


class Foreground : Service() {

    val TAG = "Foreground"

    var timer : CountUpTimer? = null
    var time by Delegates.notNull<Int>()

    val CHANNEL_ID = "FGS153"
    val NOTI_ID = 153
    var dontDie = false

    //Room 변수
    var helper : StudyDatabase? = null

    var cur : Int = 0
    var data : List<TodayTime>? = null

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

        Log.e("ACTION","ACTION : ${intent?.action.toString()}")
        when (intent?.action) {

            CustomB.Actions.START_FOREGROUND -> {
                startForegroundService(helper!!)
            }

            CustomB.Actions.STOP_FOREGROUND -> {
                dontDie = false //의도된 Stop Action 이 들어왔을때만 dontDie를 false
                stopForegroundService()
            }
        }

        helper!!.close()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    // 포그라운듯 서비스 시작
    private fun startForegroundService(helper: StudyDatabase) {
        createNotificationChannel()
        dontDie = true

        //공부 시작 시간
        val start = System.currentTimeMillis()

        data = helper.studyDAO().getTime()
        //공부하던 시간이 있으면 가져오고 아니면 0
        cur = if (data!!.isNotEmpty()) data!![0].time!! else 0

        // 타이머 (24시간짜리)
        timer = object : CountUpTimer(864000) {

            // 똑딱똑딱
            override fun onTick(second: Int) {
                // 공부하던 시간 + (현재 시간 - 시작한 시간) / 1000
                time = (cur + (System.currentTimeMillis() - start) / 1000).toInt()
                helper.studyDAO().insertTime(TodayTime(1, time))

                val notiTitle = when (time % 4) {
                    1-> "공부 중"
                    2-> "공부 중."
                    3-> "공부 중.."
                    else-> "공부 중..."
                }

                //앱 중복 실행 방지
                val intent = Intent(baseContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val pIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                //Notification Action 알림 확장버튼 (쉬는시간 버튼)
                val testIntent = Intent(baseContext, MyReceiver::class.java)
                val testPendingIntent : PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, testIntent, FLAG_ONE_SHOT)


                //알림 레이어 설정
                val layout = RemoteViews(packageName, R.layout.custom_notification)
                layout.setTextViewText(R.id.notifyTitle, notiTitle)
                layout.setTextViewText(R.id.notifyMessage, formatTime(time))

                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentTitle(null)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setCustomContentView(layout)
                    .addAction(R.drawable.plus, "쉬는타이밍!", testPendingIntent) //Notification Action 알림 확장 버튼 추가
                    .build()

                //val notificationManager =  NotificationManagerCompat.from(this)
                //notificationManager.notify(NOTI_ID, notification.build())

                startForeground(NOTI_ID, notification)
                Log.d("서비스","$time")
            }

            // 타이머 종료
            override fun onFinish() {
                super.onFinish()

                /** 죽지 말라고 했는데 죽었으면
                 *  setAlarmTimer() 로 다시 서비스 살리기
                 *  의도적으로 죽인거면 정상적으로 time 저장
                 */
                if (dontDie) {
                    helper.studyDAO().insertTime(TodayTime(1, time+1)) //시간 오차 조정 (1초)
                    setAlarmTimer()
                    Thread.currentThread().interrupt()
                }else{
                    helper.studyDAO().insertTime(TodayTime(1, time))
                }
            }
        }
        timer!!.start()
    }

    // 포그라운드 서비스 종료
    private fun stopForegroundService() {
        timer?.cancel()
        helper?.studyDAO()?.insertTime(TodayTime(1, time))
        stopForeground(true)
        stopSelf()
    }

    //시간 포맷 함수
    fun formatTime(time: Int) : String {
        val hour = String.format("%02d", time/(60*60))
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "$hour:$minute:$second"
    }

    /**  Immotal Service 시작 부분
     *   1초짜리 알람 설정하고
     *   알람이 울리면 서비스 다시 시작하는 로직
     */
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
        Log.e(TAG,"onDestroy")
        super.onDestroy()
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
