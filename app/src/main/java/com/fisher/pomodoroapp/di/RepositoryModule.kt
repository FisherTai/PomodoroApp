package com.fisher.pomodoroapp.di

import com.fisher.pomodoroapp.data.repository.HistoryRepository
import com.fisher.pomodoroapp.data.repository.HistoryRepositoryImpl
import com.fisher.pomodoroapp.data.repository.TaskRepository
import com.fisher.pomodoroapp.data.repository.TaskRepositoryImpl
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