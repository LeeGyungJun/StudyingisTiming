package com.augustin26.studyingistiming

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmRecever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("Foreground","AlarmRecever")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val `in` = Intent(context, RestartForeground::class.java)
            context.startForegroundService(`in`)
        } else {
            val `in` = Intent(context, Foreground::class.java)
            context.startService(`in`)
        }
    }
}