package com.example.pomodoroapp.di

import android.content.Context
import androidx.room.Room
import com.example.pomodoroapp.data.sources.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 1. DatabaseModule 是一個模組，負責提供與數據庫相關的依賴
 * 2. 它安裝在 SingletonComponent 中，表示提供的依賴在整個應用中共享
 * 3. 它提供兩種依賴：
 *    - AppDatabase 實例（以單例形式）
 *    - TaskDao 實例（依賴於 AppDatabase）
 *
 * ##簡單理解
 * 可以將 @Module 看作是一個「工廠」：
 * - 它告訴 Hilt：「當有人需要 AppDatabase 或 TaskDao 時，這裡有方法可以創建/提供它們」
 * - Hilt 會在有需要這些依賴的地方（比如在 ViewModel 或 Repository 中）自動調用這些方法
 * 使用 @Module 和相關註解，你不需要手動管理這些依賴項的創建和生命週期，Dagger/Hilt 會幫你處理這一切。
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /*
     *  使用 @Provides 而非 @Binds，原因在於：
     *  1. @Provides 用於創建新的對象實例，如 Room 數據庫
     *  2. AppDatabase 和 TaskDao 是由第三方提供的實例，不是由我們實現
     *  3. 創建 AppDatabase 需要額外的構建邏輯（如調用 Room.databaseBuilder()）
     *
     *  簡單規則：
     *  1. 使用 @Provides 時：
     *      - 模組應該是具體類（通常是 object）
     *      - 用於創建新實例或有複雜邏輯
     *  2. 使用 @Binds 時：
     *      - 模組必須是抽象類（abstract class）
     *      - 用於將接口綁定到實現類
     *      - 更高效，因為不需要額外的方法調用
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pomodoro_app_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideTaskDao(appDatabase: AppDatabase) = appDatabase.taskDao()

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase) = appDatabase.historyDao()
}