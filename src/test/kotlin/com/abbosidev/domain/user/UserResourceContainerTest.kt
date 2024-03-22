package com.abbosidev.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST
import org.jboss.resteasy.reactive.RestResponse.StatusCode.OK
import org.junit.jupiter.api.Test

@QuarkusTest
class UserResourceContainerTest {

    @Test
    fun `test for create a new user`() {
        val user = UserDto(
            firstname = "Abbas",
            lastname = "Donaboyev",
            username = "abbas",
            password = "123123"
        )

        given()
            .contentType(APPLICATION_JSON)
            .body(user)
            .`when`()
            .post("/api/v1/user/register")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)

        given()
            .contentType(APPLICATION_JSON)
            .body(user)
            .`when`()
            .post("/api/v1/user/register")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)

    }
}