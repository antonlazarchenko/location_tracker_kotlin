package com.alazar.base.util

import java.util.*

object CalendarUtil {

    fun getTodayStartTimeMillis(): Long {
        val calendar = Calendar.getInstance()
//        date.timeZone = TimeZone.getTimeZone(getString(R.string.gmt_timezone))
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        return calendar.timeInMillis
    }

    fun getStartTimeForDayMillis(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()

        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = day
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        return calendar.timeInMillis
    }

}