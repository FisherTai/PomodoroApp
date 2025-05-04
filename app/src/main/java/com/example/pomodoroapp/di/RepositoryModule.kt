package com.example.pomodoroapp.di

import com.example.pomodoroapp.data.repository.HistoryRepository
import com.example.pomodoroapp.data.repository.HistoryRepositoryImpl
import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.data.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository
}