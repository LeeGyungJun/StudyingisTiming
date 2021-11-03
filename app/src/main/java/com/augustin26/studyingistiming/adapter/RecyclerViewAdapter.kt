package com.augustin26.studyingistiming.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.base.BaseCalendar
import com.augustin26.studyingistiming.db.StudyDatabase
import com.augustin26.studyingistiming.ui.CustomC
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.net.URI.create
import java.util.*


class RecyclerViewAdapter(val customC: CustomC) : RecyclerView.Adapter<ViewHolderHelper>() {

    val baseCalendar = BaseCalendar()

    //Room 변수
    var helper : StudyDatabase? = null

    init {
        //달력 초기화 함수
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
        //RoomDB 빌드
        helper = Room.databaseBuilder(customC.context, StudyDatabase::class.java, "study_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        view.setOnClickListener(View.OnClickListener {

        })
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        //position은 0부터 시작한다.
        //position이 7로 나누어 떨어지면 tv_date 색상을 빨간색으로 (일요일)
        if (position % BaseCalendar.DAYS_OF_WEEK == 0) {
            holder.itemView.tv_date.setTextColor(Color.parseColor("#ff1200"))
        }else{
            holder.itemView.tv_date.setTextColor(Color.parseColor("#676d6e"))
        }

        //position < 이전 달의 마지막날짜 이거나
        //position >= 이전 달의 마지막날짜 + 현재 달의 마지막 날짜 이면 투명도를 0.3
        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.itemView.tv_date.alpha = 0.3f
        } else {
            holder.itemView.tv_date.alpha = 1f
        }

        Log.e("cal","${baseCalendar.prevMonthTailOffset} , ${baseCalendar.prevMonthTailOffset+baseCalendar.currentMonthMaxDate}")
        if (position >= baseCalendar.prevMonthTailOffset && position < baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            if (baseCalendar.thisMonthFlag && baseCalendar.data[position] == baseCalendar.nowDay) {
                //holder.itemView.tv_date.setTextColor(Color.parseColor("#380B61"))
                holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                holder.itemView.tv_date.background = customC.context.resources.getDrawable(R.drawable.today_circle)
            }
        }

        holder.itemView.tv_date.text = baseCalendar.data[position].toString()



        //baseCalendar의 년,월
        val year = baseCalendar.calendar.get(Calendar.YEAR)
        val month = baseCalendar.calendar.get(Calendar.MONTH) + 1

        //해당 년,월에 해당하는 데이터들
        val data = helper?.studyDAO()?.getStudyDate(year, month)
        Log.d("cal", "년 : $year, 월 : $month, 달력빈칸 : ${baseCalendar.prevMonthTailOffset} 데이터 : ${data}")

        if(data != null && data.isNotEmpty()) {
            for(i in 0..data.lastIndex) {
                if(position - baseCalendar.prevMonthTailOffset + 1 == data.get(i).day) {
                    val time = data[i].time?.toInt()
                    when (time) {
                        //1단 : 공부시간이 1시간 ~ 3시간
                        in 3600..10799 -> {
                        holder.itemView.background = customC.context.resources.getDrawable(R.drawable.phase1)
                        holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                        holder.itemView.setOnClickListener {
                            val builder: AlertDialog.Builder = AlertDialog.Builder(customC.context)

                            builder.setTitle("공부한 시간")
                            builder.setMessage("${formatTime(time!!)}")

                            builder.setPositiveButton("OK",
                                DialogInterface.OnClickListener { dialog, id ->
                                })
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.show()
                        }
                        }
                        //2단 : 공부시간이 3시간 ~ 5시간
                        in 10800..17999 -> {
                            holder.itemView.background = customC.context.resources.getDrawable(R.drawable.phase2)
                            holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.itemView.setOnClickListener {
                                val builder: AlertDialog.Builder = AlertDialog.Builder(customC.context)

                                builder.setTitle("공부한 시간")
                                builder.setMessage("${formatTime(time!!)}")

                                builder.setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }
                        }
                        //3단 : 공부시간이 5시간 ~ 7시간
                        in 18000..25199 -> {
                            holder.itemView.background = customC.context.resources.getDrawable(R.drawable.phase3)
                            holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.itemView.setOnClickListener {
                                val builder: AlertDialog.Builder = AlertDialog.Builder(customC.context)

                                builder.setTitle("공부한 시간")
                                builder.setMessage("${formatTime(time!!)}")

                                builder.setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }
                        }
                        //4단 : 공부시간이 7시간 ~ 9시간
                        in 25200..32399 -> {
                            holder.itemView.background = customC.context.resources.getDrawable(R.drawable.phase4)
                            holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.itemView.setOnClickListener {
                                val builder: AlertDialog.Builder = AlertDialog.Builder(customC.context)

                                builder.setTitle("공부한 시간")
                                builder.setMessage("${formatTime(time!!)}")

                                builder.setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }
                        }
                        //5단 : 공부시간이 9시간 ~
                        in 32400..99999 -> {
                            holder.itemView.background = customC.context.resources.getDrawable(R.drawable.phase5)
                            holder.itemView.tv_date.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.itemView.setOnClickListener {
                                val builder: AlertDialog.Builder = AlertDialog.Builder(customC.context)

                                builder.setTitle("공부한 시간")
                                builder.setMessage("${formatTime(time!!)}")

                                builder.setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                    })
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }
                        }
                    }
                }
            }
        }

        helper!!.close()
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        customC.refreshCurrentMonth(calendar)
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