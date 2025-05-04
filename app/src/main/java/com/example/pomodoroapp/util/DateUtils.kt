package com.example.pomodoroapp.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateUtils {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
}