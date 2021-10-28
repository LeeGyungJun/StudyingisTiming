package com.augustin26.studyingistiming.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.augustin26.studyingistiming.ui.CustomB

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CustomB(context).serviceStop()
    }
}