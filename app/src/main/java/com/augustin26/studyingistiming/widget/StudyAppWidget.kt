package com.augustin26.studyingistiming.widget

import android.app.Activity
import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.service.StudyForeground
import com.augustin26.studyingistiming.ui.CustomB
import com.augustin26.studyingistiming.ui.DialogActivity
import com.google.android.material.snackbar.Snackbar


class StudyAppWidget : AppWidgetProvider() {
    private val MyOnClick = "myOnClickTag"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.new_app_widget)
        if (MyOnClick.equals(intent?.action)){
            if (isServiceRunningCheck(context!!)) {
                //타이머 돌고있는 상태
                val intent = Intent(context, StudyForeground::class.java)
                intent.action = CustomB.Actions.STOP_FOREGROUND
                ContextCompat.startForegroundService(context, intent)

                //버튼의 이미지를 공부하는 이미지로
                remoteViews.setImageViewResource(R.id.btnStart, R.drawable.click_study)

                //홈화면에 띄울 Alert
                alertActivity(context, "stop")
                //Toast.makeText(context,"쉬는 시간!!!",Toast.LENGTH_SHORT).show()
            }else{
                //타이머가 돌지 않는 상태
                val intent = Intent(context, StudyForeground::class.java)
                intent.action = CustomB.Actions.START_FOREGROUND
                ContextCompat.startForegroundService(context, intent)

                //버튼의 이미지를 쉬는 이미지로
                remoteViews.setImageViewResource(R.id.btnStart, R.drawable.click_break)

                //홈화면에 띄울 Alert
                alertActivity(context, "start")
                //Toast.makeText(context,"공부 시작!!!", Toast.LENGTH_SHORT).show()
            }
            //컴포넌트 선언하고 매니저.updateAppWidget 로 업데이트
            val thisWidget = ComponentName(context, StudyAppWidget::class.java)
            AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, remoteViews)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.new_app_widget)
        remoteViews.setOnClickPendingIntent(R.id.btnStart, getPendingSelfIntent(context, MyOnClick))
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun alertActivity(context : Context, s : String) {
        val intent = Intent(context, DialogActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("study", s)
        ContextCompat.startActivity(context, intent, null)
    }

    protected fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun isServiceRunningCheck(context: Context) : Boolean {
        val manager: ActivityManager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.augustin26.studyingistiming.service.StudyForeground" == service.service.className) {
                return true
            }
        }
        return false
    }
}