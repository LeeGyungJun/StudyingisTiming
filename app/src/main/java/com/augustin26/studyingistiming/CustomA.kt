package com.augustin26.studyingistiming

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import kotlinx.android.synthetic.main.layout_a.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomA(context: Context?) : ConstraintLayout(context!!) {

    var helper : StudyDatabase? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_a, this, false)
        addView(view)

        helper = Room.databaseBuilder(getContext(), StudyDatabase::class.java, "study_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        val test = helper?.studyDAO()?.getDay()
        Log.e("init", "$test")

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
                    txtDdayContent.text = dialogText.text.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}