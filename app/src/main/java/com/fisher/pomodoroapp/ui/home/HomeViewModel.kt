package com.fisher.pomodoroapp.ui.home

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisher.pomodoroapp.data.repository.HistoryRepository
import com.fisher.pomodoroapp.data.repository.TaskRepository
import com.fisher.pomodoroapp.data.repository.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject




enum class TimerCommand { START, PAUSE, RESET, SKIP }


/**
 * 計時的狀態
 */
enum class CountDownState {
    RUNNING,
    PAUSE,
    STOP
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val historyRepository: HistoryRepository,
    private val timerRepository: TimerRepository,
    ) : ViewModel() {
/*
    // 用Duration替代timestamp表示
//    private val defaultFocusTimeDuration = 25.minutes // 預設專注時間
//    private val defaultBreakTimeDuration = 5.minutes //預設休息時間
//    private val defaultBigBreakTimeDuration = 20.minutes //預設大休息時間

    //快速測試用
    private val defaultFocusTimeDuration = 5.0.seconds // 預設專注時間
    private val defaultBreakTimeDuration = 5.0.seconds //預設休息時間
    private val defaultBigBreakTimeDuration = 10.0.seconds //預設大休息時間

    private var countdownJob: Job? = null
    private var cycleCount = 0 //循環的次數

    //計時的狀態
    private val _countDownState = MutableStateFlow(CountDownState.STOP)
    val countDownState: StateFlow<CountDownState> = _countDownState.asStateFlow()

    //蕃茄鐘的狀態
    private val _currentPhase = MutableStateFlow(TimerPhase.FOCUS)
    val currentPhase: StateFlow<TimerPhase> = _currentPhase.asStateFlow()

    private val currentProgressTask = taskRepository
            .getInProgressTaskFlow()
            .distinctUntilChanged() // 去重
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2000), //當有訂閱者時才啟動，在沒有訂閱者後閒置2秒才停止
                initialValue = null
            )

    // 剩餘計時時間
    private val _timeLeft = MutableStateFlow(defaultFocusTimeDuration)

    val timeDisplay: StateFlow<String> = _timeLeft
        .map { seconds ->
            DateUtils.formatElapsedTime(seconds.inWholeSeconds)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), //觀察者離開後最多再保留 5 秒。
            initialValue = DateUtils.formatElapsedTime(_timeLeft.value.inWholeSeconds)
        )

    val homeUiState: StateFlow<HomeUiState> = combine(
        countDownState,
        currentProgressTask,
        currentPhase
    ) { countDownState, currentProgressTask, currentPhase ->
        HomeUiState(
            countDownState = countDownState,
            taskDescribe = currentProgressTask?.description ?: "",
            currentPhase = currentPhase,
            onClickEvent = { onClickEvent(it) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            countDownState = CountDownState.STOP,
            taskDescribe = "",
            currentPhase = TimerPhase.FOCUS,
            onClickEvent = { onClickEvent(it) }
        )
    )

    private fun onClickEvent(event: HomeClickEvent) {
        when (event) {
            HomeClickEvent.StartPauseClicked -> toggleStartPause()
            HomeClickEvent.ResetClicked -> resetCountDown()
            HomeClickEvent.SkipPhaseClicked -> nextPhase()
        }
    }

    // 切換到下一個階段
    private fun nextPhase() {
        when (currentPhase.value) {
            TimerPhase.FOCUS -> {
                if (cycleCount >= 4) {
                    cycleCount = 0
                    _currentPhase.update { TimerPhase.BIG_BREAK }
                    _countDownState.update { CountDownState.STOP }
                    _timeLeft.update { defaultBigBreakTimeDuration }
                } else {
                    cycleCount++
                    _currentPhase.update { TimerPhase.BREAK }
                    _countDownState.update { CountDownState.STOP }
                    _timeLeft.update { defaultBreakTimeDuration }
                }
            }

            TimerPhase.BREAK, TimerPhase.BIG_BREAK -> {
                _currentPhase.update { TimerPhase.FOCUS }
                _countDownState.update { CountDownState.STOP }
                _timeLeft.update { defaultFocusTimeDuration }
            }
        }
    }

    //控制倒數計時的操作
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
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            TimerUtils.countdownFLow(_timeLeft.value.inWholeMilliseconds, 1000)
                .map { timeLeft -> timeLeft.toDuration(DurationUnit.MILLISECONDS) }
                .collect { timeLeft ->
                    _timeLeft.update { timeLeft }
                    //如果timeLeft是0
                    if (timeLeft.inWholeMilliseconds == 0L) {
                        if (currentPhase.value == TimerPhase.FOCUS){
                            onFocusComplete()
                        }
                        nextPhase()
                    }
                }
        }
    }

    private fun resetCountDown() {
        countdownJob?.cancel()
        // 重置時間
        when(currentPhase.value){
            TimerPhase.FOCUS -> _timeLeft.update { defaultFocusTimeDuration }
            TimerPhase.BREAK -> _timeLeft.update { defaultBreakTimeDuration }
            TimerPhase.BIG_BREAK -> _timeLeft.update { defaultBigBreakTimeDuration }
        }
        _countDownState.update { CountDownState.STOP }
    }

    private suspend fun onFocusComplete(){
        currentProgressTask.value?.let {
            historyRepository.insertHistory(
                HistoryEntity(
                    taskId = it.id,
                    timestamp = System.currentTimeMillis(),
                )
            )
        }
    }
*/

//    //=======================Service========================

    private val currentProgressTask = taskRepository
        .getInProgressTaskFlow()
        .distinctUntilChanged() // 去重
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2000), //當有訂閱者時才啟動，在沒有訂閱者後閒置2秒才停止
            initialValue = null
        )

    // --- 公開資料 ---
    val timeDisplay: StateFlow<String> = timerRepository.state.map {
        DateUtils.formatElapsedTime(it.millisLeft / 1000)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "00:00")

    val currentPhase = timerRepository.state.map { it.phase }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), timerRepository.state.value.phase
    )

    val isRunning = timerRepository.state.map { it.isRunning }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), timerRepository.state.value.isRunning
    )

    // CommandFlow: 由 UI 收集並根據命令決定要啟動哪個 Service action
    private val _commands = MutableSharedFlow<TimerCommand>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val commands = _commands.asSharedFlow()

    // 封裝一層給 UI 的 HomeUiState
    val homeUiState: StateFlow<HomeUiState> = combine(
        isRunning,
        currentPhase,
        currentProgressTask,
    ) { running, phase, currentProgress ->
        HomeUiState(
            countDownState = if (running) CountDownState.RUNNING else CountDownState.STOP,
            taskDescribe = currentProgress?.description ?: "", // TODO 依照 TaskRepo
            currentPhase = phase,
            onClickEvent = ::handleClick,
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(
        countDownState = CountDownState.STOP,
        taskDescribe = "",
        currentPhase = TimerPhase.FOCUS,
        onClickEvent = ::handleClick,
    ))

    private fun handleClick(event: HomeClickEvent) {
        val cmd = when (event) {
            HomeClickEvent.StartPauseClicked -> if (isRunning.value) TimerCommand.PAUSE else TimerCommand.START
            HomeClickEvent.ResetClicked -> TimerCommand.RESET
            HomeClickEvent.SkipPhaseClicked -> TimerCommand.SKIP
        }
        _commands.tryEmit(cmd)
    }
}
