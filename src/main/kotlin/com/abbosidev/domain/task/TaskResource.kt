package com.abbosidev.domain.task

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.SecurityContext

@Path("/api/v1/task")
class TaskResource(private val taskService: TaskService) {

    @WithTransaction
    @RolesAllowed("user")
    @GET
    fun getUserTasks(@Context securityContext: SecurityContext): Uni<List<TaskEntity>> =
        taskService.getAllUserTasks(securityContext.userPrincipal.name)

}