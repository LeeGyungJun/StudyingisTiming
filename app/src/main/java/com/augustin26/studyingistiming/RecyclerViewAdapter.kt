package com.augustin26.studyingistiming

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*


class RecyclerViewAdapter(val customC: CustomC) : RecyclerView.Adapter<ViewHolderHelper>() {

    val baseCalendar = BaseCalendar()

    init {
        //달력 초기화 함수
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        //position은 0부터 시작한다.
        //position이 7로 나누어 떨어지면 tv_date 색상을 빨간색으로 (일요일)
        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.itemView.tv_date.setTextColor(Color.parseColor("#ff1200"))
        else holder.itemView.tv_date.setTextColor(Color.parseColor("#676d6e"))

        //position < 이전 달의 마지막날짜 이거나
        //position >= 이전 달의 마지막날짜 + 현재 달의 마지막 날짜 이면 투명도를 0.3
        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.itemView.tv_date.alpha = 0.3f
        } else {
            holder.itemView.tv_date.alpha = 1f
        }

        if (baseCalendar.thisMonthFlag == true && baseCalendar.data[position] == baseCalendar.nowDay) {
            holder.itemView.tv_date.setTextColor(Color.parseColor("#0040FF"))
        }
        holder.itemView.tv_date.text = baseCalendar.data[position].toString()
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
}