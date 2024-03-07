package com.abbosidev.domain.task

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path

@Path("/api/v1/task")
class TaskResource {

    @RolesAllowed("user")
    @GET
    fun getUserTasks(): String {
        return "Get all tasks"
    }

}