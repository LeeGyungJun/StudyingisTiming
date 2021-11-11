package com.augustin26.studyingistiming.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.augustin26.studyingistiming.R
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_dialog)

        btn1.setOnClickListener {
            finish()
        }

        if (intent!=null) {
            if (intent.getStringExtra("study").equals("start")) {
                message.text = "공부 시작!!"
            }else if (intent.getStringExtra("study").equals("stop")) {
                message.text = "쉬는 시간!!"
            }
        }
    }
}