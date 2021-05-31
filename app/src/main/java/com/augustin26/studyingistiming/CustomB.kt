package com.augustin26.studyingistiming

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_b.view.*

class CustomB(context: Context?) : ConstraintLayout(context!!) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_b, this, false)
        addView(view)

        btnStart.setOnClickListener {
            serviceStart()
        }

        btnPause.setOnClickListener {
            serviceStop()
        }
    }


    private fun serviceStart() {
        if (isServiceRunningCheck()) {
            return
        }else{
            val intent = Intent(context, Foreground::class.java)
            intent.action = Actions.START_FOREGROUND
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context,"공부 시작!!!",Toast.LENGTH_SHORT).show()
        }
    }

    fun serviceStop() {
        if (isServiceRunningCheck()) {
            val intent = Intent(context, Foreground::class.java)
            intent.action = Actions.STOP_FOREGROUND
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context,"쉬는 시간!!!",Toast.LENGTH_SHORT).show()
        }else{
            return
        }
    }

    fun isServiceRunningCheck() : Boolean {
        val manager: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.augustin26.studyingistiming.Foreground".equals(service.service.getClassName())) {
                return true
            }
        }
        return false
    }

    object Actions {
        private const val prefix = "com.augustin26.studyingistiming.action."
        const val START_FOREGROUND = prefix + "startforeground"
        const val STOP_FOREGROUND = prefix + "stopforeground"
    }

}