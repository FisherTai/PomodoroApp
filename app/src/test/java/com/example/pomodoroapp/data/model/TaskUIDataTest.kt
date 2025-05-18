package com.example.pomodoroapp.data.model

import com.example.pomodoroapp.data.sources.database.TaskEntity
import com.example.pomodoroapp.data.sources.database.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskUIDataTest {

    // 測試當 TaskEntity 的狀態為 _TODO 時 轉換成 TaskUIData 的 isChoose 應該是 false
    @Test
    fun buildFromTaskEntity_withTodoStatus_isNotChosen() {
        val taskEntity = TaskEntity(
            id = 1,
            description = "Test Task",
            status = TaskStatus.TODO
        )
        val result = TaskUIData.build(taskEntity)

        assertEquals(1, result.id)
        assertEquals("Test Task", result.description)
        assertFalse(result.isChoose)
    }

    // 測試當 TaskEntity 的狀態為 IN_PROGRESS 時 轉換成 TaskUIData 的 isChoose 應該是 true
    @Test
    fun buildFromTaskEntity_withInProgressStatus_isChosen() {
        val taskEntity = TaskEntity(
            id = 2,
            description = "In Progress Task",
            status = TaskStatus.IN_PROGRESS
        )
        val result = TaskUIData.build(taskEntity)

        assertEquals(2, result.id)
        assertEquals("In Progress Task", result.description)
        assertTrue(result.isChoose)
    }

    // 測試當 TaskEntity 的狀態為 COMPLETED 時 轉換成 TaskUIData 的 isChoose 應該是 false
    @Test
    fun buildFromTaskEntity_withCompletedStatus_isNotChosen() {

        val taskEntity = TaskEntity(
            id = 3,
            description = "Completed Task",
            status = TaskStatus.COMPLETED
        )

        val result = TaskUIData.build(taskEntity)

        assertEquals(3, result.id)
        assertEquals("Completed Task", result.description)
        assertFalse(result.isChoose)
    }
}