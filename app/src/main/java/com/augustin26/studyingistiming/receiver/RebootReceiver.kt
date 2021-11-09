package com.augustin26.studyingistiming.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.augustin26.studyingistiming.service.StudyForeground
import com.augustin26.studyingistiming.service.RestartForeground

class RebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(context, RestartForeground::class.java)
            context.startForegroundService(intent)
        } else {
            val intent = Intent(context, StudyForeground::class.java)
            context.startService(intent)
        }
    }
}