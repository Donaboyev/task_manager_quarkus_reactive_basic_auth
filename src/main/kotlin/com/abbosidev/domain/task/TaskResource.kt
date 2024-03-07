package com.abbosidev.domain.task

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST

@Path("/api/v1/task")
class TaskResource(private val taskService: TaskService) {

    @WithTransaction
    @RolesAllowed("user")
    @GET
    fun getUserTasks(@Context securityContext: SecurityContext): Uni<List<TaskEntity>> =
        taskService.getAllUserTasks(securityContext.userPrincipal.name)

    @WithTransaction
    @RolesAllowed("user")
    @POST
    fun saveNewTask(@Context securityContext: SecurityContext, task: TaskDto): Uni<Response> =
        taskService
            .saveNewTask(securityContext.userPrincipal.name, task)
            .onItem()
            .ifNotNull()
            .transform { entity -> Response.ok(entity).build() }
            .onItem()
            .ifNull()
            .continueWith {
                val message = HashMap<String, String>().apply {
                    put("message", "Something went wrong")
                }
                Response.status(BAD_REQUEST).entity(message).build()
            }

    @WithTransaction
    @RolesAllowed("user")
    @GET
    @Path("/{id}")
    fun getTaskById(@PathParam("id") id: Long): Uni<Response> =
        taskService
            .getTaskById(id)
            .onItem()
            .ifNotNull()
            .transform { entity -> Response.ok(entity).build() }
            .onItem()
            .ifNull()
            .continueWith {
                val message = HashMap<String, String>().apply {
                    put("message", "Task with $id id does not exist")
                }
                Response.status(BAD_REQUEST).entity(message).build()
            }
}