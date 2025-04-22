package com.example.pomodoroapp.ui.home

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class HomeUiState(
    val timeString: String = "25:00",
    val countDownState: CountDownState = CountDownState.STOP,
    val taskDescribe: String = "測試任務"
)

// 2. UI Event
//sealed class HomeEvent {
//    object StartPauseClicked : HomeEvent()
//    object ResetClicked : HomeEvent()
//}

class HomeViewModel : ViewModel() {

    // 用Duration替代timestamp表示
    val defaultTimeDuration = 25.minutes

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _countDownState = MutableStateFlow(CountDownState.STOP)
    val countDownState: StateFlow<CountDownState> = _countDownState.asStateFlow()

    private val _taskDescribe = MutableStateFlow("測試任務")
    val taskDescribe: StateFlow<String> = _taskDescribe.asStateFlow()

    private val _timeLeft = MutableStateFlow(defaultTimeDuration)
    val timeLeft: StateFlow<Duration> = _timeLeft.asStateFlow()

    fun setTimeLeft(timeLeft: Duration) {
        _timeLeft.update { timeLeft }
    }

    fun resetTimer(){
        setTimeLeft(defaultTimeDuration)
    }

    val timeDisplay: StateFlow<String> = _timeLeft
        .map { seconds ->
            DateUtils.formatElapsedTime(seconds.inWholeSeconds)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //觀察者離開後最多再保留 5 秒。
            initialValue = DateUtils.formatElapsedTime(_timeLeft.value.inWholeSeconds)
        )

    fun setCountDownState(state: CountDownState){
        _countDownState.update { state }
    }

    fun pause(){
        _countDownState.update { CountDownState.PAUSE }
    }

    fun start(){
        _countDownState.update { CountDownState.RUNNING }
    }



//
//    private var countdownJob: Job? = null

//    fun onEvent(event: HomeEvent) {
//        when (event) {
//            HomeEvent.StartPauseClicked -> startTimer()
////                toggleStartPause()
//            HomeEvent.ResetClicked -> reset()
//        }
//    }

//    private fun toggleStartPause() {
////        val current = _uiState.value
//        if (countDownState == CountDownState.RUNNING) {
//            countdownJob?.cancel()
//            _uiState.update { it.copy(countDownState = CountDownState.PAUSE) }
//        } else {
//            _uiState.update { it.copy(countDownState = CountDownState.RUNNING) }
//            startTimer()
//        }
//    }

//    private fun startTimer() {
//        countdownJob = viewModelScope.launch {
//            TimerUtils.countdownFLow(timeLeft.value.inWholeMilliseconds, 1000)
//                .map { timeLeft -> timeLeft.toDuration(kotlin.time.DurationUnit.MILLISECONDS) }
//                .collect { timeLeft ->
//                    _timeLeft.update { timeLeft }
//                }
//        }
//    }

//    private fun reset() {
//        countdownJob?.cancel()
//        _uiState.update {
//            it.copy(
//                timeString = DateUtils.formatElapsedTime(25.minutes.inWholeSeconds),
//                countDownState = CountDownState.STOP
//            )
//        }
//    }
}

enum class CountDownState {
    RUNNING,
    PAUSE,
    STOP
}
