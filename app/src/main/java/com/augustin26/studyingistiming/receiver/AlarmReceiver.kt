package com.augustin26.studyingistiming.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.augustin26.studyingistiming.service.StudyForeground
import com.augustin26.studyingistiming.service.RestartForeground

/**
 *  서비스가 죽었을때 알람 리시버
 *  Oreo 이하 버전이면 startService로 해당 서비스를 바로 실행하고
 *  Oreo 이후 버전은 startForegroundService로 한 단계를 더 거쳐야 한다.
 *  (Oreo 에서는 서비스를 백그라운드에서 실행하는 것을 금지하기 때문에 포그라운드에서 실행)
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("StudyForeground","AlarmRecever")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(context, RestartForeground::class.java)
            context.startForegroundService(intent)
        } else {
            val intent = Intent(context, StudyForeground::class.java)
            context.startService(intent)
        }
    }
}