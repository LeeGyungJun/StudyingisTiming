package com.augustin26.studyingistiming.ui

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.augustin26.studyingistiming.Const
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.adapter.CustomPagerAdapter
import com.augustin26.studyingistiming.receiver.StudyReceiver
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val br : BroadcastReceiver = StudyReceiver()
        val filter = IntentFilter().apply{
            addAction("android.intent.action.DATE_CHANGED")
        }
        registerReceiver(br, filter)

        // 플로팅 버튼 클릭시 에니메이션 동작 기능
        fabMain.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트 - 유튜부
        fabYoutube.setOnClickListener {
            //startActivity(Intent(this, YoutubeActivity::class.java))
        }
    }


    /***
     *  플로팅 액션 버튼 클릭시 동작하는 애니메이션 효과 세팅
     */
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(fabYoutube, "translationY", 0f).apply { start() }
            fabMain.setImageResource(R.drawable.plus)

            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            ObjectAnimator.ofFloat(fabYoutube, "translationY", -200f).apply { start() }
            fabMain.setImageResource(R.drawable.close)
        }

        isFabOpen = !isFabOpen

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

        if (intent!=null) {
            if (intent.getBooleanExtra(Const.Receiver.DATE_CHANGE, false)) {
                val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("알림")
                builder.setMessage("날짜가 변경되어 화면을 업데이트합니다.")
                builder.setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                    })
                builder.show()
                intent = null
            }
        }
        super.onResume()
    }

}