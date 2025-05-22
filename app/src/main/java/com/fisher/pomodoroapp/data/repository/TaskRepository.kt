package com.fisher.pomodoroapp.data.repository

import com.fisher.pomodoroapp.data.sources.database.TaskDao
import com.fisher.pomodoroapp.data.sources.database.TaskEntity
import com.fisher.pomodoroapp.data.sources.database.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRepository {
    val tasks: Flow<List<TaskEntity>>
    suspend fun addTask(title: String, isNew : Boolean = true): Int
    suspend fun deleteTask(task: TaskEntity)
    suspend fun closeTask(id: Int)
    suspend fun updateTask(task: TaskEntity)
    suspend fun updateTaskDescription(id: Int, description: String)
    suspend fun updateTasks(tasks: List<TaskEntity>)
    suspend fun getTask(id: Int): TaskEntity?
    suspend fun getInProgressTask(): List<TaskEntity>
    fun getInProgressTaskFlow(): Flow<TaskEntity?>
}

@Singleton
class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {

    override val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasksExceptNotActive()

    override suspend fun addTask(title: String, isNew : Boolean): Int =
        taskDao.insertTask(
            TaskEntity(
                description = title,
                status = if (isNew) TaskStatus.IN_PROGRESS else TaskStatus.TODO,
            )
        ).toInt()

    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    override suspend fun closeTask(id: Int) = taskDao.changeTaskActiveStatus(id, TaskStatus.DELETED)

    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    override suspend fun updateTaskDescription(id: Int, description: String) = taskDao.updateTaskDescription(id, description)

    override suspend fun updateTasks(tasks: List<TaskEntity>) = taskDao.updateTasks(tasks)

    override suspend fun getTask(id: Int): TaskEntity = taskDao.getTaskById(id)

    override suspend fun getInProgressTask(): List<TaskEntity> = taskDao.getInProgressTask()

    override fun getInProgressTaskFlow(): Flow<TaskEntity?> = taskDao.getInProgressTaskFlow()
}