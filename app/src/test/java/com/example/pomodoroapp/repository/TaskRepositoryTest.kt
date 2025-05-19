package com.example.pomodoroapp.repository

import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.data.repository.TaskRepositoryImpl
import com.example.pomodoroapp.data.sources.database.TaskDao
import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    /*
     * mockk  模擬 DAO 和 DB
     * coEvery	結果驗證：定義呼叫假的 DAO 時回傳什麼
     * coVerify	用於行為測試：驗證 Repository 有沒有正確呼叫 DAO 方法
     * runTest	讓測試支援 suspend 函數與協程邏輯
     * flowOf(...)	當 DAO 的方法回傳的是一個 Flow 時，模擬封裝的 Flow 結果
     */

    // 模擬TaskDao
    private lateinit var mockTaskDao: TaskDao

    // 被測試的對象
    private lateinit var taskRepository: TaskRepository

    // 測試資料
    private val testTasks = listOf(
        TaskEntity(id = 1, description = "Task 1", status = TaskStatus.TODO),
        TaskEntity(id = 2, description = "Task 2", status = TaskStatus.IN_PROGRESS),
        TaskEntity(id = 3, description = "Task 3", status = TaskStatus.COMPLETED)
    )

    @Before
    fun setUp() {
        // 初始化mock
        mockTaskDao = mockk(relaxed = true)

        // 配置模擬行為
        coEvery { mockTaskDao.getAllTasksExceptNotActive() } returns flowOf(testTasks)
        coEvery { mockTaskDao.getTaskById(any()) } answers {
            val id = firstArg<Int>()
            testTasks.find { it.id == id }!!
        }

        // 創建Repository實例
        taskRepository = TaskRepositoryImpl(mockTaskDao)
    }

    @Test
    fun `獲取任務列表應該返回所有非刪除任務`() = runTest {
        // 執行
        val result = taskRepository.tasks.first()

        // 驗證
        assertEquals(3, result.size)
        assertEquals("Task 1", result[0].description)
    }

    @Test
    fun `通過ID獲取任務應該返回正確任務`() = runTest {
        // 執行
        val result = taskRepository.getTask(2)

        // 驗證
        assertEquals("Task 2", result?.description)
        coVerify { mockTaskDao.getTaskById(2) }
    }

    @Test
    fun `添加任務應該調用DAO的插入方法`() = runTest {
        // 設置
        val taskDescription = "New Task"
        coEvery { mockTaskDao.insertTask(any()) } returns 4L

        // 執行
        val result = taskRepository.addTask(taskDescription)

        // 驗證
        assertEquals(4, result)
        coVerify {
            mockTaskDao.insertTask(match {
                it.description == taskDescription &&
                        it.status == TaskStatus.IN_PROGRESS
            })
        }
    }

    @Test
    fun `關閉任務應該將任務狀態更新為已刪除`() = runTest {
        // 設置
        val taskId = 1

        // 執行
        taskRepository.closeTask(taskId)

        // 驗證
        coVerify { mockTaskDao.changeTaskActiveStatus(taskId, TaskStatus.DELETED) }
    }
}