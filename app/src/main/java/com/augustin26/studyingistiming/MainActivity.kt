package com.augustin26.studyingistiming

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_a.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val br : BroadcastReceiver = StudyReceiver()
        val filter = IntentFilter().apply{
            addAction("android.intent.action.DATE_CHANGED")
        }
        registerReceiver(br, filter)
    }

    override fun onResume() {
        //사용할 뷰 클래스를 모두 생성해서 views 변수에 담는다.
        val views:List<View> = listOf(CustomA(this), CustomB(this), CustomC(this))

        //커스텀 어댑터를 생성
        val adapter = CustomPagerAdapter()
        //생성해둔 뷰 클래스 목록을 adapter에 담는다.
        adapter.views = views
        //viewPager에 adapter를 연결한다.
        viewPager.adapter = adapter
        super.onResume()
    }
}