package com.example.nevaketo

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi

fun getDayOfTheWeek(calendar: Calendar): String {
    return when(calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "PN"
        Calendar.TUESDAY -> "VT"
        Calendar.WEDNESDAY -> "SR"
        Calendar.THURSDAY -> "CT"
        Calendar.FRIDAY -> "PT"
        Calendar.SATURDAY -> "SB"
        Calendar.SUNDAY -> "VS"
        else -> ""
    }

}

