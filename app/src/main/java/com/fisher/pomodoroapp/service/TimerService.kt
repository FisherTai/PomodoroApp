package com.fisher.pomodoroapp.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.fisher.pomodoroapp.data.repository.TimerRepository
import com.fisher.pomodoroapp.ui.home.TimerPhase
import com.fisher.pomodoroapp.ui.home.TimerState
import com.fisher.pomodoroapp.util.TimerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject lateinit var repo: TimerRepository
    @Inject lateinit var helper: NotificationHelper

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        helper.createChannel()
        startForeground(NOTI_ID, helper.build(repo.state.value))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START  -> start()
            ACTION_PAUSE  -> pause()
            ACTION_RESET  -> reset()
            ACTION_SKIP   -> skipPhase()
        }
        return START_STICKY
    }

    private fun start() {
        if (repo.state.value.isRunning) return
        repo.update { it.copy(isRunning = true) }
        launchTimer()
    }

    private fun pause() {
        job?.cancel()
        repo.update { it.copy(isRunning = false) }
        notifyUI()
    }

    private fun reset() {
        job?.cancel()
        val phase = repo.state.value.phase
        repo.update {
            it.copy(
                millisLeft = phaseDefault(phase),
                isRunning = false
            )
        }
        notifyUI()
    }

    private fun skipPhase() {
        job?.cancel()
        advancePhase()
        repo.update { it.copy(isRunning = false) }
        notifyUI()
    }

    private fun launchTimer() {
        job?.cancel()
        job = scope.launch {
            val start = repo.state.first()
            TimerUtils.countdownFLow(start.millisLeft, 1000).collect { m ->
                repo.update { it.copy(millisLeft = m) }
                if (m == 0L) advancePhase()
                notifyUI()
            }
        }
    }

    private fun advancePhase() {
        val s = repo.state.value
        val next = when (s.phase) {
            TimerPhase.FOCUS -> TimerPhase.BREAK
            TimerPhase.BREAK -> TimerPhase.FOCUS
            TimerPhase.BIG_BREAK -> TimerPhase.FOCUS
        }
        repo.update { it.copy(phase = next, millisLeft = phaseDefault(next)) }
    }

    private fun phaseDefault(phase: TimerPhase) = when (phase) {
        TimerPhase.FOCUS -> TimerState.DEFAULT_FOCUS.inWholeMilliseconds
        TimerPhase.BREAK -> TimerState.DEFAULT_BREAK.inWholeMilliseconds
        TimerPhase.BIG_BREAK -> TimerState.DEFAULT_BIG_BREAK.inWholeMilliseconds
    }

    @SuppressLint("MissingPermission") // 跟第一次 startForeground() 的通知相同ID，前景通知更新同一顆ID時不需要權限(?
    private fun notifyUI() {
        NotificationManagerCompat.from(this)
            .notify(NOTI_ID, helper.build(repo.state.value))
    }



    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "pomodoro_timer"
        const val NOTI_ID = 1001

        const val ACTION_START = "com.fisher.pomodoroapp.action.START"
        const val ACTION_PAUSE = "com.fisher.pomodoroapp.action.PAUSE"
        const val ACTION_RESET = "com.fisher.pomodoroapp.action.RESET"
        const val ACTION_SKIP  = "com.fisher.pomodoroapp.action.SKIP"
    }
}
