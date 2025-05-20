package com.example.pomodoroapp.ui.tasks

import app.cash.turbine.test
import com.example.pomodoroapp.data.model.TaskUIData
import com.example.pomodoroapp.data.repository.TaskRepository
import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class TaskListViewModelTest {

    // 測試專用的Dispatchers
    private val testDispatcher = StandardTestDispatcher()

    // 測試用的Repository Mock
    private lateinit var fakeTaskRepository: FakeTaskRepository
    private lateinit var viewModel: TaskListViewModel

    // 測試資料
    private val testTasks = listOf(
        TaskEntity(id = 1, description = "Task 1", status = TaskStatus.TODO),
        TaskEntity(id = 2, description = "Task 2", status = TaskStatus.IN_PROGRESS),
        TaskEntity(id = 3, description = "Task 3", status = TaskStatus.COMPLETED)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeTaskRepository = FakeTaskRepository(testTasks)
        viewModel = TaskListViewModel(fakeTaskRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `測試初始任務列表加載`() = runTest {
        // 先推進協程調度器，確保數據流已處理
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.tasks.test {
            // 獲取初始值（空列表）
            val emptyList = awaitItem()
            // 然後獲取數據加載後的值
            val tasks = awaitItem()
            assertEquals(3, tasks.size)
            assertEquals("Task 1", tasks[0].description)
            assertEquals("Task 2", tasks[1].description)
            assertEquals("Task 3", tasks[2].description)
        }
    }

    @Test
    fun `測試選擇任務`() = runTest {
        // 先確保有一個進行中的任務
        fakeTaskRepository.addTask("In Progress Task", isNew = true)

        // 選擇另一個任務
        val taskToSelect = TaskUIData(id = 1, description = "Task 1", isChoose = false)
        viewModel.onClickEvent(TaskScreenClickEvent.SelectTask(taskToSelect))

        testDispatcher.scheduler.advanceUntilIdle() // 確保協程完成

        // 驗證：
        // 1. 之前的進行中任務應被設為 TODO
        // 2. 選中的任務應變為 IN_PROGRESS
        val updatedTasks = fakeTaskRepository.updatedTasksList

        assertTrue(updatedTasks?.any { it.id == 1 && it.status == TaskStatus.IN_PROGRESS } ?: false)
        assertTrue(updatedTasks?.all { it.id != 1 || it.status == TaskStatus.IN_PROGRESS } ?: false)
    }

    @Test
    fun `測試添加新任務`() = runTest {
        // 先確保有一個進行中的任務
        fakeTaskRepository.addTask("In Progress Task", isNew = true)

        // 添加新任務
        viewModel.onClickEvent(TaskScreenClickEvent.AddNewTask("New Task"))

        testDispatcher.scheduler.advanceUntilIdle() // 確保協程完成

        // 驗證：
        // 1. 新任務已添加且為進行中
        // 2. 之前的進行中任務變為 TODO
        assertEquals("New Task", fakeTaskRepository.lastAddedTaskDescription)

        // 檢查之前的進行中任務是否被重置
        val updatedTasks = fakeTaskRepository.updatedTasksList
        assertTrue(updatedTasks?.all { it.status == TaskStatus.TODO } ?: false)
    }

    @Test
    fun `測試刪除任務`() = runTest {
        val taskToDelete = TaskUIData(id = 1, description = "Task 1", isChoose = false)
        viewModel.onClickEvent(TaskScreenClickEvent.DeleteTask(taskToDelete))

        testDispatcher.scheduler.advanceUntilIdle() // 確保協程完成

        assertEquals(1, fakeTaskRepository.lastClosedTaskId)
    }

    @Test
    fun `測試編輯任務`() = runTest {
        val taskToEdit = TaskUIData(id = 1, description = "Updated Task", isChoose = false)
        viewModel.onClickEvent(TaskScreenClickEvent.EditTask(taskToEdit))

        testDispatcher.scheduler.advanceUntilIdle() // 確保協程完成

        assertEquals(1, fakeTaskRepository.lastUpdatedTaskId)
        assertEquals("Updated Task", fakeTaskRepository.lastUpdatedDescription)
    }
}

/**
 * 測試用的TaskRepository實現
 */
class FakeTaskRepository(initialTasks: List<TaskEntity>) : TaskRepository {
    private val tasksFlow = MutableStateFlow(initialTasks)

    // 追蹤方法調用
    var lastAddedTaskDescription: String? = null
    var lastClosedTaskId: Int? = null
    var lastSelectedTaskId: Int? = null
    var lastUpdatedTaskId: Int? = null
    var lastUpdatedDescription: String? = null
    var updatedTasksList: List<TaskEntity>? = null

    override val tasks: Flow<List<TaskEntity>> = tasksFlow

    override suspend fun addTask(title: String, isNew: Boolean): Int {
        lastAddedTaskDescription = title
        val newId = tasksFlow.value.maxOfOrNull { it.id }?.plus(1) ?: 1
        val newTask = TaskEntity(
            id = newId,
            description = title,
            status = if (isNew) TaskStatus.IN_PROGRESS else TaskStatus.TODO
        )
        tasksFlow.value = tasksFlow.value + newTask
        return newId
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksFlow.value = tasksFlow.value.filter { it.id != task.id }
    }

    override suspend fun closeTask(id: Int) {
        lastClosedTaskId = id
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == id) it.copy(status = TaskStatus.DELETED) else it
        }
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksFlow.value = tasksFlow.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun updateTaskDescription(id: Int, description: String) {
        lastUpdatedTaskId = id
        lastUpdatedDescription = description
        tasksFlow.value = tasksFlow.value.map {
            if (it.id == id) it.copy(description = description) else it
        }
    }

    override suspend fun updateTasks(tasks: List<TaskEntity>) {
        updatedTasksList = tasks
        tasksFlow.value = tasksFlow.value.map { original ->
            tasks.find { it.id == original.id } ?: original
        }
    }

    override suspend fun getTask(id: Int): TaskEntity? {
        lastSelectedTaskId = id
        return tasksFlow.value.find { it.id == id }
    }

    override suspend fun getInProgressTask(): List<TaskEntity> {
        return tasksFlow.value.filter { it.status == TaskStatus.IN_PROGRESS }
    }

    override fun getInProgressTaskFlow(): Flow<TaskEntity?> {
        return MutableStateFlow(tasksFlow.value.find { it.status == TaskStatus.IN_PROGRESS })
    }
}
