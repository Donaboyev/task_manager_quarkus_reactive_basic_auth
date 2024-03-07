package com.abbosidev.domain.task

import com.abbosidev.domain.user.UserEntity
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import io.smallrye.mutiny.Uni
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class TaskEntity : PanacheEntity() {

    companion object : PanacheCompanion<TaskEntity> {

        fun getAllUserTasks(username: String) = find("user.username", username).list()

        fun save(username: String, task: TaskDto): Uni<TaskEntity?> =
            UserEntity.getUserByUsername(username).flatMap { principal ->
                if (principal == null) Uni.createFrom().nullItem()
                else
                    TaskEntity().apply {
                        title = task.title
                        description = task.description
                        dueDate = task.dueDate
                        user = principal
                    }.persist()
            }


    }

    lateinit var title: String
    lateinit var description: String
    lateinit var dueDate: LocalDate

    @ManyToOne
    lateinit var user: UserEntity
}