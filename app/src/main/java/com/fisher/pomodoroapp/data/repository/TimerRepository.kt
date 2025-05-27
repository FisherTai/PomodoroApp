package com.fisher.pomodoroapp.data.repository

import com.fisher.pomodoroapp.ui.home.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton


/**
 * 倒計時的 Repository ，作為 Service 跟 HomeViewModel 之間的唯一資料來源
 * Service 更新、HomeViewModel 讀取。
 */
@Singleton
class TimerRepository @Inject constructor() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    internal fun update(block: (TimerState) -> TimerState) = _state.update(block)
}