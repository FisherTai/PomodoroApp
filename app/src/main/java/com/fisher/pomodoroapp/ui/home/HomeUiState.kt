package com.fisher.pomodoroapp.ui.home

/**
 * UI State
 */
data class HomeUiState(
    val countDownState: CountDownState,
    val taskDescribe: String,
    val currentPhase: TimerPhase,
    val onClickEvent: (event: HomeClickEvent) -> Unit
) {
    fun hasTask() = taskDescribe.isNotBlank()
    fun isFocus() = currentPhase == TimerPhase.FOCUS
    fun isBreak() = currentPhase == TimerPhase.BREAK
}

/**
 * UI Event
 */
sealed class HomeClickEvent {
    data object StartPauseClicked : HomeClickEvent()
    data object ResetClicked : HomeClickEvent()
    data object SkipPhaseClicked : HomeClickEvent()
}