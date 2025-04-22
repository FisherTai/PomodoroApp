package com.example.pomodoroapp.util

import android.os.CountDownTimer

class PauseAbleCountDownTimer(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
    private val onTick: (Long) -> Unit,
    private val onCancel: () -> Unit,
    private val onComplete: () -> Unit,
) {
    private var timer: CountDownTimer? = null
    private var timeLeft: Long = millisInFuture
    private var isRunning = false

    fun start() {
        timer = object : CountDownTimer(timeLeft, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                this@PauseAbleCountDownTimer.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                isRunning = false
                this@PauseAbleCountDownTimer.onCancel()
            }
        }.start()
        isRunning = true
    }

    fun pause() {
        timer?.cancel()
        isRunning = false
    }

    fun cancel() {
        timer?.cancel()
        timeLeft = millisInFuture
        isRunning = false
    }
}
