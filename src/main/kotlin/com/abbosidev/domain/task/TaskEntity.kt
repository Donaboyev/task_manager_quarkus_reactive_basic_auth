package com.abbosidev.domain.task

import com.abbosidev.domain.user.UserEntity
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class TaskEntity : PanacheEntity() {

    companion object : PanacheCompanion<TaskEntity> {

        fun getAllUserTasks(username: String) = find("user.username", username).list()

    }

    lateinit var title: String
    lateinit var description: String
    lateinit var dueDate: LocalDate

    @ManyToOne
    lateinit var user: UserEntity
}