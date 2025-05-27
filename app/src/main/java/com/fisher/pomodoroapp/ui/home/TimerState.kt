package com.fisher.pomodoroapp.ui.home

import kotlin.time.Duration.Companion.minutes

/**
 * 當前 HomeScreen 的階段
 */
enum class TimerPhase { FOCUS, BREAK, BIG_BREAK }

data class TimerState(
    val millisLeft: Long = DEFAULT_FOCUS.inWholeMilliseconds,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val isRunning: Boolean = false,
) {
    companion object {
        val DEFAULT_FOCUS = 25.minutes
        val DEFAULT_BREAK = 5.minutes
        val DEFAULT_BIG_BREAK = 20.minutes
    }
}