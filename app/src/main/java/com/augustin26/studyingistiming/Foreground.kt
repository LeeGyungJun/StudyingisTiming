package com.augustin26.studyingistiming

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import java.security.AccessController.getContext
import kotlin.concurrent.thread

class Foreground : Service() {

    val CHANNEL_ID = "FGS153"
    val NOTI_ID = 153
    var started = false

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

        when (intent?.action) {
            CustomB.Actions.START_FOREGROUND -> startForegroundService(helper!!)
            CustomB.Actions.STOP_FOREGROUND -> stopForegroundService()
        }
        helper!!.close()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    private fun startForegroundService(helper: StudyDatabase) {
        createNotificationChannel()
        started = true
        thread(start=true) {
            while (started) {
                Thread.sleep(1000)

                val data = helper?.studyDAO()?.getTime()

                var time: Int? = if (data!!.size > 0) {
                    data!!.get(0).time!!
                }else{
                    0
                }

                time = time!! + 1
                helper.studyDAO().insertTime(TodayTime(1, time))

                var notiTitle = when (time % 4) {
                    1-> "공부 중"
                    2-> "공부 중."
                    3-> "공부 중.."
                    else-> "공부 중..."
                }

                //앱 중복 실행 방지
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val pIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


                //알림 레이어 설정
                val layout = RemoteViews(packageName, R.layout.custom_notification)
                layout.setTextViewText(R.id.notifyTitle, notiTitle)
                layout.setTextViewText(R.id.notifyMessage, formatTime(time))

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setOngoing(true)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .setCustomContentView(layout)
                    .build()

                //val notificationManager =  NotificationManagerCompat.from(this)
                //notificationManager.notify(NOTI_ID, notification.build())

                startForeground(NOTI_ID, notification)
                Log.d("서비스","$time")
            }
        }
    }

    private fun stopForegroundService() {
        started = false
        stopForeground(true)
        stopSelf()
    }

    //시간 포맷 함수
    fun formatTime(time:Int) : String {
        val hour = String.format("%02d", time/(60*60))
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "$hour:$minute:$second"
    }

}