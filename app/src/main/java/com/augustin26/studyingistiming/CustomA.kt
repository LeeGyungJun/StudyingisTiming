package com.augustin26.studyingistiming

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import io.realm.Realm
import kotlinx.android.synthetic.main.layout_a.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CustomA(context: Context?) : LinearLayoutCompat(context!!) {

    var realmManager: RealmManager

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_a, this, false)
        addView(view)

        realmManager = RealmManager(Realm.getDefaultInstance())

        val Dday = realmManager.findDay(1)?.day
        txtDday.text = Dday.toString()
        Log.d("init","Dday : $Dday")
        val DdayContent = realmManager.findContent(1)?.content
        txtDdayContent.text = DdayContent
        Log.d("init", "DdayContent : $DdayContent")

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
                val data = Dday()
                data.day = D_day
                realmManager.updateDay(1, data)

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
                    val data = DdayContent()
                    data.content = dialogText.toString()
                    realmManager.updateContent(1, data)
                    txtDdayContent.text = dialogText.text.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}