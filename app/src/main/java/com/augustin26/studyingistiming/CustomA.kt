package com.augustin26.studyingistiming

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.*
import kotlinx.android.synthetic.main.layout_a.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomA(context: Context?) : ConstraintLayout(context!!) {

    //Room 변수
    var helper : StudyDatabase? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_a, this, false)
        addView(view)

        //RoomDB 빌드
        helper = Room.databaseBuilder(getContext(), StudyDatabase::class.java, "study_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        //dday_day 테이블을 조회하여 d1에 저장
        val d1 = helper?.studyDAO()?.getDay()
        var day: Long? = null
        if (d1!!.size > 0) {
            //조회한 테이블(배열)의 사이즈가 0보다 크면
            //테이블의 0번째 컬럼(Dday클래스)의 day 값을 day에 저장한다.
            day = d1!!.get(0)?.day
        }

        //dday_content 테이블을 조회하여 d2에 저장
        val d2 = helper?.studyDAO()?.getContent()
        var content: String? = null
        if (d2!!.size > 0) {
            //조회한 테이블(배열)의 사이즈가 0보다 크면
            //테이블의 0번째 컬럼(DdayContent클래스)의 content 값을 content에 저장한다.
            content = d2!!.get(0)?.content
        }

        if (day != null) {
            if(day > 0) {
                txtDday.text = "D-${day}"
            } else if (day < 0) {
                txtDday.text = "D+${day*(-1)}"
            } else {
                txtDday.text = "D-day"
            }
        }
        if (content != null) txtDdayContent.text = content

        //디데이 날짜 설정 클릭 이벤트
        view.txtDday.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            var listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dateFormat = SimpleDateFormat("yyyyMd")
                val endDate = dateFormat.parse("${year}${month+1}${day}").time
                Log.d("Dday","${year}${month+1}${day}")

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time
                Log.d("Dday", "${today}")

                var D_day = (endDate - today) / (24 * 60 * 60 * 1000)

                val data = Dday(1,D_day)
                helper?.studyDAO()?.insertDay(data)

                if(D_day > 0) {
                    txtDday.text = "D-${D_day}"
                } else if (D_day < 0) {
                    txtDday.text = "D+${D_day*(-1)}"
                } else {
                    txtDday.text = "D-day"
                }
            }
            var picker = context?.let { it1 -> DatePickerDialog(it1, listener, year, month, day) }
            picker?.show()
        }

        //디데이 내용 설정 클릭 이벤트
        view.txtDdayContent.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
            val dialogText = dialogView.findViewById<EditText>(R.id.etDialog)

            builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    val data = DdayContent(1,dialogText.text.toString())
                    helper?.studyDAO()?.insertContent(data)
                    txtDdayContent.text = dialogText.text.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}