package com.augustin26.studyingistiming

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_b.view.*

class CustomB(context: Context?) : ConstraintLayout(context!!) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_b, this, false)
        addView(view)

        btnStart.setOnClickListener {
            serviceStart(view)
        }

        btnPause.setOnClickListener {
            serviceStop(view)
        }
    }


    private fun serviceStart(view: View) {
        if (isServiceRunningCheck()) {
            return
        }else{
            val intent = Intent(context, Foreground::class.java)
            intent.action = Actions.START_FOREGROUND
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun serviceStop(view: View) {
        val intent = Intent(context, Foreground::class.java)
        intent.action = Actions.STOP_FOREGROUND
        ContextCompat.startForegroundService(context, intent)
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