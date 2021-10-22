package com.augustin26.studyingistiming

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*


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
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.home).text = ""
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.book).text = ""
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.calendar).text = ""
        super.onResume()
    }

}