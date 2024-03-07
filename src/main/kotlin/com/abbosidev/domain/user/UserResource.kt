package com.abbosidev.domain.user

import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST

@Path("/api/v1/user")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class UserResource(private val userService: UserService) {

    @WithTransaction
    @POST
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

}