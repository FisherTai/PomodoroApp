package com.fisher.pomodoroapp.util

import java.time.Instant

fun Long.toDateString(dateUtils: DateUtils): String{
    val date = Instant.ofEpochMilli(this).atZone(dateUtils.zone).toLocalDate()
    return if (date.isEqual(dateUtils.today)) {
        "Today"
    } else {
        date.format(dateUtils.dateFormatter)
    }
}