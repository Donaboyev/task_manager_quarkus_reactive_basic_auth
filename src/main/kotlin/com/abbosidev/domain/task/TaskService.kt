package com.abbosidev.domain.task

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskService {

    fun getAllUserTasks(username: String) = TaskEntity.getAllUserTasks(username)

    fun saveNewTask(username: String, task: TaskDto) = TaskEntity.save(username, task)

    fun getTaskById(id: Long) = TaskEntity.findById(id)
}