package com.fisher.pomodoroapp.di

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object TimerModule {
    @Provides
    fun notificationManager(@ApplicationContext ctx: Context): NotificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
