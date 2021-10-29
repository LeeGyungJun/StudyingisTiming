package com.augustin26.studyingistiming.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.*
import com.augustin26.studyingistiming.Dday
import com.augustin26.studyingistiming.DdayContent
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.db.StudyDatabase
import kotlinx.android.synthetic.main.layout_a.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class CustomA(context: Context?) : ConstraintLayout(context!!) {

    private val PREFS_NAME = "_pref_name"
    private val DDAY = "_dday"
    private val DDAY_CONTENT = "_dday_content"


    private var sharedPreference: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    //Room 변수
    var helper : StudyDatabase? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_a, this, false)
        addView(view)

        sharedPreference = this.context!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference!!.edit()

        //RoomDB 빌드
        helper = Room.databaseBuilder(getContext(), StudyDatabase::class.java, "study_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

//        //dday_day 테이블을 조회하여 d1에 저장
//        val d1 = helper?.studyDAO()?.getDay()
//        var day: Long? = null
//        if (d1!!.isNotEmpty()) {
//            //조회한 테이블(배열)의 사이즈가 0보다 크면
//            //테이블의 0번째 컬럼(Dday클래스)의 day 값을 day에 저장한다.
//            day = d1[0].day
//        }
//
//        //dday_content 테이블을 조회하여 d2에 저장
//        val d2 = helper?.studyDAO()?.getContent()
//        var content: String? = null
//        if (d2!!.isNotEmpty()) {
//            //조회한 테이블(배열)의 사이즈가 0보다 크면
//            //테이블의 0번째 컬럼(DdayContent클래스)의 content 값을 content에 저장한다.
//            content = d2[0].content
//        }


        //디데이로 설정한 날짜
        val day = sharedPreference!!.getLong(DDAY, 0)
        //디데이 내용
        val content = sharedPreference!!.getString(DDAY_CONTENT, "디데이")
        //오늘 날짜
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time


        //어제 내 공부시간
        val d3 = helper?.studyDAO()?.getStudy()
        var yesterdayStudyTime: Int? = null
        if (d3!!.size > 0) {
            yesterdayStudyTime = d3[d3.lastIndex].time
        }
        val D_day: Long

        if (day != 0.toLong()) {//디데이로 설정한 날짜에서 오늘 날짜를 빼줌
            D_day = (day - today) / (24 * 60 * 60 * 1000)

            //디데이
            if(D_day > 0) {
                txtDday.text = "D-${D_day}"
            } else if (D_day < 0) {
                txtDday.text = "D+${D_day*(-1)}"
            } else {
                txtDday.text = "D-day"
            }
            Log.d("Dday", "${D_day}")
        }

        //디데이 내용
        if (content != null) txtDdayContent.text = content

        //어제 공부한 시간
        if (yesterdayStudyTime != null) {
            txtYesterdayTime.text = formatTime(yesterdayStudyTime)
        }

        //디데이 날짜 설정 클릭 이벤트
        view.txtDday.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dateFormat = if (month<9) SimpleDateFormat("yyyyMdd") else SimpleDateFormat("yyyyMMdd")
                val endDate = dateFormat.parse("${year}${month+1}${day}").time
                Log.d("Dday","${year}${month+1}${day}")

                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time
                Log.d("Dday", "${today}")

                val D_day = (endDate - today) / (24 * 60 * 60 * 1000)
                Log.d("Dday", "e ${endDate/ (24 * 60 * 60 * 1000)}")
                Log.d("Dday", "t ${today/ (24 * 60 * 60 * 1000)}")

//                val data = Dday(1,D_day)
//                helper?.studyDAO()?.insertDay(data)

                editor!!.putLong(DDAY, endDate).apply()

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
//                    val data = DdayContent(1,dialogText.text.toString())
//                    helper?.studyDAO()?.insertContent(data)
                    editor!!.putString(DDAY_CONTENT, dialogText.text.toString()).apply()
                    txtDdayContent.text = dialogText.text.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }

    //시간 포맷 함수
    fun formatTime(time:Int) : String {
        val hour = String.format("%d", time/(60*60))
        val minute = String.format("%d", (time/60)%60)
        val second = String.format("%d", time%60)
        if (hour == "0") {
            return "${minute}분 ${second}초"
        }
        return "${hour}시간 ${minute}분 ${second}초"
    }
}