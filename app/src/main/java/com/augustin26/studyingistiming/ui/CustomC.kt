package com.augustin26.studyingistiming.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.augustin26.studyingistiming.base.BaseCalendar
import com.augustin26.studyingistiming.R
import com.augustin26.studyingistiming.adapter.RecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_c.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomC(context: Context?) : LinearLayoutCompat(context!!) {

    lateinit var scheduleRecyclerViewAdapter: RecyclerViewAdapter

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_c, this, false)
        addView(view)

        initView()
    }

    fun initView() {

        scheduleRecyclerViewAdapter = RecyclerViewAdapter(this)
        rv_schedule.layoutManager = GridLayoutManager(context, BaseCalendar.DAYS_OF_WEEK)
        rv_schedule.adapter = scheduleRecyclerViewAdapter
        rv_schedule.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        rv_schedule.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }
}