package com.augustin26.studyingistiming

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

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
            manager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_NONE
                )
            )
        }
        val notification = NotificationCompat.Builder(applicationContext, "default")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(null)
            .setContentText(null)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(9, notification)

        /////////////////////////////////////////////////////////////////////
        val intent = Intent(this, Foreground::class.java)
        intent.action = Actions.START_FOREGROUND
        ContextCompat.startForegroundService(this, intent)
//        startService(`in`)
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