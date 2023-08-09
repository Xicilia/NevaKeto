package com.example.nevaketo

import java.time.LocalDate
import java.util.Calendar
import java.util.Date

fun getDayDifferenceBetweenDates(firstDate: Calendar, secondDate: Calendar): Int {
    val timeDiff = firstDate.timeInMillis - secondDate.timeInMillis

    return (timeDiff / 86400000).toInt()
}

fun getDateObjectByString(dateString: String): Calendar {
    val dateElements = dateString.split(".")

    return Calendar.getInstance().also {
        it.set(dateElements[2].toInt(),dateElements[1].toInt(),dateElements[0].toInt())
    }
}

fun getTimeNormalized(hour: Int, minute: Int): String {
    return "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"
}

