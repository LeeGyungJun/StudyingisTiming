package com.augustin26.studyingistiming

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.RoomDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter
import java.util.*

class BaseCalendar() {

    companion object {
        //달력의 가로 (일~토)
        const val DAYS_OF_WEEK = 7
        //달력의 세로 (6줄)
        const val LOW_OF_CALENDAR = 6
    }

    val calendar = Calendar.getInstance()

    val now: Long = System.currentTimeMillis()
    val date = Date(now)
    val dateFormatYear = SimpleDateFormat("yyyy", Locale("ko", "KR"))
    val dateFormatMonth = SimpleDateFormat("M", Locale("ko", "KR"))
    val dateFormatDay = SimpleDateFormat("d", Locale("ko", "KR"))

    val nowYear = dateFormatYear.format(date).toInt()
    val nowMonth = dateFormatMonth.format(date).toInt()
    val nowDay = dateFormatDay.format(date).toInt()

    //이전 달의 마지막 날짜
    var prevMonthTailOffset = 0
    //다음 달의 첫번째 날짜
    var nextMonthHeadOffset = 0
    //이번 달의 마지막 날짜
    var currentMonthMaxDate = 0
    //현재 날짜에 속한 달인지 확인할 flag
    var thisMonthFlag = false

    //날짜를 담을 List
    //이 리스트에는 이전 달의 날짜도 있고, 현재 달의 1일 부터 ~ 마지막 날짜까지도 있고, 다음 달의 1일~ 도 있다. 총 42개
    var data = arrayListOf<Int>()

    init {
        //현재 시간을 calendar.time에 넣는다. (setTime 함수)
        calendar.time = Date()
    }

    fun initBaseCalendar(refreshCallback: (Calendar) -> Unit) {
        makeMonthDate(refreshCallback)
    }

    //calendar를 이전 달로 바꿔주는 함수
    fun changeToPrevMonth(refreshCallback: (Calendar) -> Unit) {
        //현재의 달이 1월이면
        if(calendar.get(Calendar.MONTH) == 0){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }
        makeMonthDate(refreshCallback)
    }

    //calendar를 다음 달로 바꿔주는 함수
    fun changeToNextMonth(refreshCallback: (Calendar) -> Unit) {
        if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER){
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
            calendar.set(Calendar.MONTH, 0)
        }else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
        }
        makeMonthDate(refreshCallback)
    }

    private fun makeMonthDate(refreshCallback: (Calendar) -> Unit) {

        //날짜 List를 초기화
        data.clear()

        //set() : 주어진 달력 필드를 주어진 값으로 설정.
        //달력을 현재 달의 1일로 설정
        calendar.set(Calendar.DATE, 1)

        //현재 달인지 확인하는 플래그
        thisMonthFlag = nowYear == calendar.get(Calendar.YEAR) && nowMonth == calendar.get(Calendar.MONTH)+1

        //현재 달의 마지막 날짜
        //getActualMaximum() : 이 캘린더의 시간 값이 주어지면 지정된 캘린더 필드가 가질 수있는 최대 값을 리턴
        //이번 달의 마지막 날짜가 저장됨.
        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        //이전 달의 마지막 날짜
        //get() : 주어진 달력 필드의 값을 반환.
        //현재 요일 - 1 한 값이 prevMonthTailOffset에 저장됨. (참고 : 오늘이 1일)
        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1

        //clone() : 이 개체의 복사본을 만들고 반환.
        makePrevMonthTail(calendar.clone() as Calendar) //현재 달력을 인자로 makePrevMonthTail을 실행
        makeCurrentMonth(calendar)                      //이번 달의 1~31일까지를 data에 넣음.

        //2021년 5월 기준 : 6 * 7 - (6 + 31)
        //달력을 채울 마지막 달의 일 수를 계산하여 nextMonthHeadOffset에 저장
        nextMonthHeadOffset = LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)
        makeNextMonthHead()                             //달력을 마저 채움.

        refreshCallback(calendar)
    }

    private fun makePrevMonthTail(calendar: Calendar) {
        //달력의 월을 calendar.get(Canlendar.Month)-1)로 설정
        //Calendar.Month - 1 이면 이전 달
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        //이전 달의 최대 날짜를 maxDate에 저장
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        //최대 날짜 - (현재 요일 -1) 한 값이 maxOffsetDate에 저장
        var maxOffsetDate = maxDate - prevMonthTailOffset

        //이전 달의 꼬리부분이 data에 저장됨.
        for (i in 1..prevMonthTailOffset) data.add(++maxOffsetDate)
    }

    private fun makeCurrentMonth(calendar: Calendar) {
        //이번 달의 처음부터 끝까지 data에 저장됨.
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) data.add(i)
    }

    private fun makeNextMonthHead() {
        var date = 1

        for (i in 1..nextMonthHeadOffset) data.add(date++)
    }
}