package com.example.yeezlemobileapp.utils


import java.util.Calendar
import java.util.TimeZone

class CountdownTimer {

    private fun getMidnightUTC(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun fetchReferenceTimeFromServer() : Long {
        val referenceTimeMillis = getMidnightUTC()
        return  referenceTimeMillis
    }
}