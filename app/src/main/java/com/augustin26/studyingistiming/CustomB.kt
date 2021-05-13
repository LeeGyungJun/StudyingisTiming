package com.augustin26.studyingistiming

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_b.view.*
import kotlin.concurrent.thread

class CustomB(context: Context?) : ConstraintLayout(context!!) {
    //시작 변수
    var started = false
    //시간 변수
    var time = 0

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_b, this, false)
        addView(view)

        //공부 타이밍 버튼
        btnStart.setOnClickListener {
            start()
            Toast.makeText(getContext(), "타이머를 시작합니다.", Toast.LENGTH_SHORT).show()
        }
        //쉬는 타이밍 버튼
        btnPause.setOnClickListener {
            pause()
            Toast.makeText(getContext(), "타이머를 정지합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //핸들러 메시지들
    val START_TIMER = 51
    val STOP_TIMER = 50

    //타이머 핸들러
    private val handler = object: Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                //공부 중
                START_TIMER -> {
                    val time = msg.arg1
                    txtTime.text = formatTime(time)
                }
                //리셋
                STOP_TIMER -> {
                    txtTime.text = formatTime(time)
                }
            }
        }
    }

    //타이머 시작 함수
    fun start() {
        started = true
        thread(start=true) {
            while (started) {
                Thread.sleep(1000)
                if(!started) break
                time++

                val msg = Message()
                msg.what = START_TIMER
                msg.arg1 = time
                handler.sendMessage(msg)
            }
        }
    }

    //타이머 멈춤 함수
    fun pause() { started = false }

    //타이머 리셋 함수
    fun reset() {
        started = false
        time = 0
        val msg = Message()
        msg.what = STOP_TIMER
        msg.arg1 = time
        handler.sendMessage(msg)
    }

    //시간 포맷 함수
    fun formatTime(time:Int) : String {
        val hour = String.format("%02d", time/(60*60))
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "$hour:$minute:$second"
    }

}