package com.abbosidev.domain.user

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST
import org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND

@Path("/api/v1/user")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class UserResource(private val userService: UserService) {

    @WithTransaction
    @POST
    @Path("/register")
    fun registerNewUser(user: UserDto): Uni<Response> = userService
        .saveNewUser(user)
        .onItem()
        .ifNotNull()
        .transform { entity ->
            Response.ok(entity).build()
        }
        .onItem()
        .ifNull()
        .continueWith {
            val message = HashMap<String, String>().apply {
                put("message", "User already exists")
            }
            Response.status(BAD_REQUEST).entity(message).build()
        }

    @WithTransaction
    @GET
    @Path("/login")
    fun login(@Context securityContext: SecurityContext): Uni<Response> = userService
        .login(securityContext.userPrincipal.name)
        .onItem()
        .ifNotNull()
        .transform { entity ->
            Response.ok(entity).build()
        }
        .onItem()
        .ifNull()
        .continueWith {
            val message = HashMap<String, String>().apply {
                put("message", "User not found")
            }
            Response.status(NOT_FOUND).entity(message).build()
        }

    @RolesAllowed("user")
    @WithTransaction
    @DELETE
    @Path("/{id}")
    fun deleteUser(@PathParam("id") id: Long): Uni<Response> = userService
        .deleteUserById(id)
        .onItem()
        .transform { deleted ->
            if (deleted) {
                val message = HashMap<String, String>().apply {
                    put("message", "User with $id id successfully deleted.")
                }
                Response.ok(message).build()
            } else {
                val message = HashMap<String, String>().apply {
                    put("message", "User with $id id does not exist.")
                }
                Response.status(BAD_REQUEST).entity(message).build()

            }
        }
}