package com.augustin26.studyingistiming.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.augustin26.studyingistiming.ui.CustomB

/**
 *   Notification Action 알림 확장 버튼
 *   (공부 중.. 알림을 아래로 슬라이드해서 나오는 버튼을 클릭했을때 처리하는 리시버)
 */
class NotiStopReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        CustomB(context).serviceStop()
    }
}