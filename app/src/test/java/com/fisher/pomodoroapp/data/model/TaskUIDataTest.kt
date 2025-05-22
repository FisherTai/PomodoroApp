package com.fisher.pomodoroapp.data.model

import com.fisher.pomodoroapp.data.sources.database.TaskEntity
import com.fisher.pomodoroapp.data.sources.database.TaskStatus
import org.junit.Assert
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

        Assert.assertEquals(1, result.id)
        Assert.assertEquals("Test Task", result.description)
        Assert.assertFalse(result.isChoose)
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

        Assert.assertEquals(2, result.id)
        Assert.assertEquals("In Progress Task", result.description)
        Assert.assertTrue(result.isChoose)
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

        Assert.assertEquals(3, result.id)
        Assert.assertEquals("Completed Task", result.description)
        Assert.assertFalse(result.isChoose)
    }
}