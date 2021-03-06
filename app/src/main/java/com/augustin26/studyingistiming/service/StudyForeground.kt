package com.augustin26.studyingistiming.service

import android.app.*
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.TodayTime
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.receiver.AlarmReceiver
import com.augustin26.studyingistiming.receiver.NotiStopReceiver
import com.augustin26.studyingistiming.ui.CustomB
import com.augustin26.studyingistiming.ui.MainActivity
import java.util.*
import kotlin.properties.Delegates


class StudyForeground : Service() {

    val TAG = "StudyForeground"

    var timer : CountUpTimer? = null
    var time by Delegates.notNull<Int>()

    val CHANNEL_ID = "FGS153"
    val NOTI_ID = 153
    var dontDie = false

    //Room λ³μ
    var helper : StudyDatabase? = null

    var cur : Int = 0
    var data : List<TodayTime>? = null

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID,
                "FOREGROUND", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //RoomDB λΉλ
        helper = Room.databaseBuilder(this, StudyDatabase::class.java, "study_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        Log.e("ACTION","ACTION : ${intent?.action.toString()}")
        when (intent?.action) {

            CustomB.Actions.START_FOREGROUND -> {
                startForegroundService(helper!!)
            }

            CustomB.Actions.STOP_FOREGROUND -> {
                dontDie = false //STOP_FOREGROUND Actionμ΄ λ€μ΄μμλλ§ dontDieλ₯Ό false
                stopForegroundService()
            }
        }

        helper!!.close()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    // ν¬κ·ΈλΌμ΄λ― μλΉμ€ μμ
    private fun startForegroundService(helper: StudyDatabase) {
        createNotificationChannel()
        dontDie = true

        //κ³΅λΆμμ μκ°
        val start = System.currentTimeMillis()

        data = helper.studyDAO().getTime()
        //κ³΅λΆνλ μκ°μ΄ μμΌλ©΄ κ°μ Έμ€κ³  μλλ©΄ 0
        cur = if (data!!.isNotEmpty()) data!![0].time!! else 0

        // νμ΄λ¨Έ (24μκ°μ§λ¦¬, 86400000λ°λ¦¬μ΄)
        timer = object : CountUpTimer(86400000) {

            // λλ±λλ±
            override fun onTick(second: Int) {
                // κ³΅λΆνλμκ° + (νμ¬μκ° - μμνμκ°) / 1000
                time = (cur + (System.currentTimeMillis() - start) / 1000).toInt()
                helper.studyDAO().insertTime(TodayTime(1, time))

                val notiTitle = when (time % 4) {
                    1-> "κ³΅λΆ μ€"
                    2-> "κ³΅λΆ μ€."
                    3-> "κ³΅λΆ μ€.."
                    else-> "κ³΅λΆ μ€..."
                }

                //μ± μ€λ³΅ μ€ν λ°©μ§
                val intent = Intent(baseContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val pIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                //Notification Action μλ¦Ό νμ₯λ²νΌ (μ¬λμκ° λ²νΌ)
                val actionIntent = Intent(baseContext, NotiStopReceiver::class.java)
                val actionPendingIntent : PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, actionIntent, FLAG_ONE_SHOT)

                //μλ¦Ό λ μ΄μ΄ μ€μ 
                val layout = RemoteViews(packageName, R.layout.custom_notification)
                layout.setTextViewText(R.id.notifyTitle, notiTitle)
                layout.setTextViewText(R.id.notifyMessage, formatTime(time))

                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
                    setContentTitle(null)
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setOngoing(true)
                    setContentIntent(pIntent)
                    setAutoCancel(true)
                    setCustomContentView(layout)
                    addAction(R.drawable.plus, "μ¬λνμ΄λ°!", actionPendingIntent) //Notification Action μλ¦Ό νμ₯ λ²νΌ μΆκ°
                }.build()

                startForeground(NOTI_ID, notification)
                Log.d("μλΉμ€","$time")
            }

            // νμ΄λ¨Έ μ’λ£
            override fun onFinish() {
                super.onFinish()

                /**
                 *  μ£½μ§ λ§λΌκ³  νλλ° μ£½μμΌλ©΄
                 *  setAlarmTimer() λ‘ λ€μ μλΉμ€ μ΄λ¦¬κΈ°
                 *  μλμ μΌλ‘ μ£½μΈκ±°λ©΄ μ μμ μΌλ‘ time μ μ₯
                 */

                if (dontDie) {
                    helper.studyDAO().insertTime(TodayTime(1, time+1)) //μλΉμ€ μ¬μμ λ‘μ§μ λν μκ°μ€μ°¨ μ‘°μ  (1μ΄)
                    setAlarmTimer()
                    Thread.currentThread().interrupt()
                }else{
                    helper.studyDAO().insertTime(TodayTime(1, time))
                }
            }
        }
        timer!!.start()
    }

    // ν¬κ·ΈλΌμ΄λ μλΉμ€ μ’λ£
    private fun stopForegroundService() {
        timer?.cancel()
        helper?.studyDAO()?.insertTime(TodayTime(1, time))
        stopForeground(true)
        stopSelf()
    }

    //μκ° ν¬λ§· ν¨μ
    fun formatTime(time: Int) : String {
        val hour = String.format("%02d", time/(60*60))
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "$hour:$minute:$second"
    }

    /**
     *   Immotal Service μμ λΆλΆ
     *   1μ΄μ§λ¦¬ μλ μ€μ νκ³ 
     *   μλμ΄ μΈλ¦¬λ©΄ μλλ¦¬μλ² -> μλΉμ€ λ€μμμ νλ λ‘μ§
     */
    fun setAlarmTimer() {
        val c: Calendar = Calendar.getInstance()
        c.setTimeInMillis(System.currentTimeMillis())
        c.add(Calendar.SECOND, 1)
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        val mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, sender)
        Log.e("StudyForeground","setAlarmTimer")
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")
        super.onDestroy()
    }

    abstract class CountUpTimer protected constructor(private val duration: Long) :
        CountDownTimer(duration, INTERVAL_MS) {
        abstract fun onTick(second: Int)
        override fun onTick(msUntilFinished: Long) {
            val second = ((duration - msUntilFinished) / 1000).toInt()
            onTick(second)
        }

        override fun onFinish() {
            onTick(duration / 1000)
        }

        companion object {
            private const val INTERVAL_MS: Long = 1000
        }
    }
}
