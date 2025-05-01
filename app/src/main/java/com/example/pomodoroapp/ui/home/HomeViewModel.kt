package com.example.pomodoroapp.ui.home

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.util.TimerUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toDuration

// 2. UI Event
sealed class HomeClickEvent {
    data object StartPauseClicked : HomeClickEvent()
    data object ResetClicked : HomeClickEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // 用Duration替代timestamp表示
    private val defaultTimeDuration = 25.minutes
    private var countdownJob: Job? = null

    private val _countDownState = MutableStateFlow(CountDownState.STOP)
    val countDownState: StateFlow<CountDownState> = _countDownState.asStateFlow()

    val taskDescribe: StateFlow<String> = taskRepository
        .getInProgressTaskFlow()
        .map { it.firstOrNull()?.description.orEmpty() }
        .distinctUntilChanged() // 忽略重复描述
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000), //當有訂閱者時才啟動，在沒有訂閱者後閒置2秒才停止
            initialValue = ""                                  // 如果需要可以改成默认描述
        )

    private val _timeLeft = MutableStateFlow(defaultTimeDuration)

    val timeDisplay: StateFlow<String> = _timeLeft
        .map { seconds ->
            DateUtils.formatElapsedTime(seconds.inWholeSeconds)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //觀察者離開後最多再保留 5 秒。
            initialValue = DateUtils.formatElapsedTime(_timeLeft.value.inWholeSeconds)
        )

    fun onClickEvent(event: HomeClickEvent) {
        when (event) {
            HomeClickEvent.StartPauseClicked -> toggleStartPause()
            HomeClickEvent.ResetClicked -> resetCountDown()
        }
    }

    private fun toggleStartPause() {
        if (countDownState.value == CountDownState.RUNNING) {
            countdownJob?.cancel()
            _countDownState.update { CountDownState.PAUSE }
        } else {
            startTimer()
            _countDownState.update { CountDownState.RUNNING }
        }
    }

    private fun startTimer() {
        countdownJob = viewModelScope.launch {
            TimerUtils.countdownFLow(_timeLeft.value.inWholeMilliseconds, 1000)
                .map { timeLeft -> timeLeft.toDuration(kotlin.time.DurationUnit.MILLISECONDS) }
                .collect { timeLeft ->
                    _timeLeft.update { timeLeft }
                }
        }
    }

    private fun resetCountDown() {
        countdownJob?.cancel()
        _timeLeft.update { defaultTimeDuration } // 重置時間
        _countDownState.update { CountDownState.STOP }
    }
}

enum class CountDownState {
    RUNNING,
    PAUSE,
    STOP
}
