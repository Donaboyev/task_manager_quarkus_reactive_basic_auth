package com.abbosidev.domain.user

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.common.mapper.TypeRef
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.hamcrest.core.Is.`is`
import org.jboss.resteasy.reactive.RestResponse.StatusCode.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserResourceTest {

    private val tempFirstname = "TEMP_FIRSTNAME"
    private val tempLastname = "TEMP_LASTNAME"
    private val tempUsername = "TEMP_USERNAME"
    private val tempPassword = "TEMP_PASSWORD"
    private val userAlreadyExists = "User already exists"
    private val dummyUsername = "$$$ Dummy Username $$$"
    private val dummyPassword = "$$$ Dummy Password $$$"
    private val userWith = "User with"
    private val idSuccessfullyDeleted = "id successfully deleted."
    private val idDoesNotExist = "id does not exist."
    private val dummyUserId = -1

    companion object {
        var currentUserId = -1L
    }

    @Order(1)
    @Test
    fun `test for registering and deleting user`() {
        val user = UserDto(
            firstname = tempFirstname,
            lastname = tempLastname,
            username = tempUsername,
            password = tempPassword
        )

        val savedUser = given()
            .contentType(APPLICATION_JSON)
            .body(user)
            .`when`()
            .post("/api/v1/user/register")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getUserEntityTypeRef())

        assertNotNull(savedUser.id)

        currentUserId = savedUser.id!!

        assertEquals(savedUser.firstname, tempFirstname)
        assertEquals(savedUser.lastname, tempLastname)
        assertEquals(savedUser.username, tempUsername)

        given()
            .contentType(APPLICATION_JSON)
            .body(user)
            .`when`()
            .post("/api/v1/user/register")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body("message", `is`(userAlreadyExists))
    }

    @Order(2)
    @Test
    fun `test for login`() {
        val loggedInUser = given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .`when`()
            .get("/api/v1/user/login")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getUserEntityTypeRef())

        assertNotNull(loggedInUser.id)
        assertEquals(loggedInUser.firstname, tempFirstname)
        assertEquals(loggedInUser.lastname, tempLastname)
        assertEquals(loggedInUser.username, tempUsername)

        given()
            .auth()
            .preemptive()
            .basic(dummyUsername, dummyPassword)
            .`when`()
            .get("/api/v1/user/login")
            .then()
            .statusCode(UNAUTHORIZED)
    }

    @Order(3)
    @Test
    fun `test for deleting`() {
        given()
            .auth()
            .preemptive()
            .basic(dummyUsername, dummyPassword)
            .pathParams("id", currentUserId)
            .`when`()
            .delete("/api/v1/user/{id}")
            .then()
            .statusCode(UNAUTHORIZED)

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", dummyUserId)
            .`when`()
            .delete("/api/v1/user/{id}")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$userWith $dummyUserId $idDoesNotExist"))

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentUserId)
            .`when`()
            .delete("/api/v1/user/{id}")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$userWith $currentUserId $idSuccessfullyDeleted"))

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentUserId)
            .`when`()
            .delete("/api/v1/user/{id}")
            .then()
            .statusCode(UNAUTHORIZED)

    }

    fun getUserEntityTypeRef() = object : TypeRef<UserEntity>() {}
}