package com.augustin26.studyingistiming.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.ui.MainActivity

//StudyForeground를 실행시켜줄 포그라운드 서비스

class RestartForeground : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.e("RestartForeground","onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("RestartForeground","onStartCommand")

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel("default","기본 채널",NotificationManager.IMPORTANCE_NONE))
        }
        val notification = NotificationCompat.Builder(applicationContext, "default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(null)
            .setContentText(null)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(9, notification)
        //포그라운드 알림이 켜져있는 동안 실행할 작업
        val intent = Intent(this, StudyForeground::class.java)
        intent.action = Actions.START_FOREGROUND
        ContextCompat.startForegroundService(this, intent)

        //위 작업을 끝내면 자기 자신을 멈춘다.
        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    object Actions {
        private const val prefix = "com.augustin26.studyingistiming.action."
        const val START_FOREGROUND = prefix + "startforeground"
        const val STOP_FOREGROUND = prefix + "stopforeground"
    }

}