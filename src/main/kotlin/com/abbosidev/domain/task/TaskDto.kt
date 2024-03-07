package com.abbosidev.domain.task

import java.time.LocalDate

data class TaskDto(
    val title: String,
    val description: String,
    val dueDate: LocalDate
)