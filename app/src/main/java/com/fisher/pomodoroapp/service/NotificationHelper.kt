package com.fisher.pomodoroapp.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fisher.pomodoroapp.R
import com.fisher.pomodoroapp.ui.home.TimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val nm: NotificationManager
) {
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    TimerService.CHANNEL_ID,
                    ctx.getString(R.string.channel_name_timer),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    fun build(timer: TimerState) =
        NotificationCompat.Builder(ctx, TimerService.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_focus) //鬧鐘icon
            .setContentTitle(ctx.getString(R.string.app_name))
            .setContentText(android.text.format.DateUtils.formatElapsedTime(timer.millisLeft / 1000))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                if (timer.isRunning) R.drawable.icon_focus else R.drawable.icon_break,
                if (timer.isRunning) ctx.getString(R.string.btn_pause) else ctx.getString(R.string.btn_start),
                actionPI(if (timer.isRunning) TimerService.ACTION_PAUSE else TimerService.ACTION_START)
            )
            .addAction(
                R.drawable.icon_break,
                ctx.getString(R.string.btn_reset),
                actionPI(TimerService.ACTION_RESET)
            )
            .build()

    private fun actionPI(action: String) = PendingIntent.getService(
        ctx,
        action.hashCode(),
        Intent(ctx, TimerService::class.java).apply { this.action = action },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
