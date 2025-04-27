package com.example.pomodoroapp.data.repository

import com.example.pomodoroapp.data.sources.database.TaskDao
import com.example.pomodoroapp.data.sources.database.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface TaskRepository {
    val tasks: Flow<List<TaskEntity>>
    suspend fun addTask(title: String): Int
    suspend fun deleteTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun getTask(id: Int): TaskEntity
}

@Singleton
class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {

    override val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override suspend fun addTask(title: String): Int =
        taskDao.insertTask(
            TaskEntity(
                description = title
            )
        ).toInt()

    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    override suspend fun getTask(id: Int): TaskEntity = taskDao.getTaskById(id)

}