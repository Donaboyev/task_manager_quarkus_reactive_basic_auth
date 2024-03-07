package com.abbosidev.domain.task

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class TaskService {

    fun getAllUserTasks(username: String) = TaskEntity.getAllUserTasks(username)

}