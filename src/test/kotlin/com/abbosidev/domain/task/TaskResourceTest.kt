package com.abbosidev.domain.task

import com.abbosidev.domain.user.UserDto
import com.abbosidev.domain.user.UserEntity
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
import java.time.LocalDate

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TaskResourceTest {

    private val tempFirstname = "TEMP_FIRSTNAME"
    private val tempLastname = "TEMP_LASTNAME"
    private val tempUsername = "TEMP_USERNAME"
    private val tempPassword = "TEMP_PASSWORD"
    private val tempTaskTitle = "TEMP_TASK_TITLE"
    private val tempTaskDescription = "TEMP_TASK_DESCRIPTION"
    private val tempTaskDueDate = LocalDate.now()
    private val dummyTaskId = -1
    private val dummyUserId = -1
    private val updatedTaskTitle = "UPDATED_TASK_TITLE"
    private val updatedTaskDescription = "UPDATED_TASK_DESCRIPTION"
    private val updatedTaskDueDate = tempTaskDueDate.plusDays(1)
    private val taskWith = "Task with"
    private val userWith = "User with"
    private val idSuccessfullyDeleted = "id successfully deleted."
    private val idDoesNotExist = "id does not exist."

    companion object {
        var currentUserId = -1L
        var currentTaskId = -1L
    }

    @Order(1)
    @Test
    fun `test creating new task`() {
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
            .`when`()
            .post("/api/v1/task")
            .then()
            .statusCode(UNAUTHORIZED)

        val task = TaskDto(
            title = tempTaskTitle,
            description = tempTaskDescription,
            dueDate = tempTaskDueDate
        )

        val savedTask = given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .contentType(APPLICATION_JSON)
            .body(task)
            .post("/api/v1/task")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getTaskEntityTypeRef())

        assertNotNull(savedTask.id)

        currentTaskId = savedTask.id!!

        assertEquals(savedTask.title, tempTaskTitle)
        assertEquals(savedTask.description, tempTaskDescription)
        assertEquals(savedTask.dueDate, tempTaskDueDate)
        assertEquals(savedTask.user.firstname, tempFirstname)
        assertEquals(savedTask.user.lastname, tempLastname)
        assertEquals(savedTask.user.username, tempUsername)
        assertEquals(savedTask.user.id, currentUserId)
    }

    @Order(2)
    @Test
    fun `test getting single task`() {
        given()
            .pathParams("id", currentTaskId)
            .`when`()
            .get("/api/v1/task/{id}")
            .then()
            .statusCode(UNAUTHORIZED)

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", dummyTaskId)
            .`when`()
            .get("/api/v1/task/{id}")
            .then()
            .statusCode(BAD_REQUEST)
            .body("message", `is`("$taskWith $dummyTaskId $idDoesNotExist"))

        val currentTask = given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentTaskId)
            .`when`()
            .get("/api/v1/task/{id}")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getTaskEntityTypeRef())

        assertNotNull(currentTask.id)
        assertEquals(currentTask.title, tempTaskTitle)
        assertEquals(currentTask.description, tempTaskDescription)
        assertEquals(currentTask.dueDate, tempTaskDueDate)
        assertEquals(currentTask.user.firstname, tempFirstname)
        assertEquals(currentTask.user.lastname, tempLastname)
        assertEquals(currentTask.user.username, tempUsername)
        assertEquals(currentTask.user.id, currentUserId)
    }

    @Order(3)
    @Test
    fun `test updating task`() {
        given()
            .pathParams("id", currentTaskId)
            .`when`()
            .put("/api/v1/task/{id}")
            .then()
            .statusCode(UNAUTHORIZED)

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentTaskId)
            .`when`()
            .put("/api/v1/task/{id}")
            .then()
            .statusCode(UNSUPPORTED_MEDIA_TYPE)

        val task = TaskDto(
            title = updatedTaskTitle,
            description = updatedTaskDescription,
            dueDate = updatedTaskDueDate
        )

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", dummyTaskId)
            .contentType(APPLICATION_JSON)
            .body(task)
            .`when`()
            .put("/api/v1/task/{id}")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$taskWith $dummyTaskId $idDoesNotExist"))

        val updated = given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentTaskId)
            .contentType(APPLICATION_JSON)
            .body(task)
            .`when`()
            .put("/api/v1/task/{id}")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getTaskEntityTypeRef())

        assertNotNull(updated.id)
        assertEquals(updated.title, updatedTaskTitle)
        assertEquals(updated.description, updatedTaskDescription)
        assertEquals(updated.dueDate, updatedTaskDueDate)
        assertEquals(updated.user.firstname, tempFirstname)
        assertEquals(updated.user.lastname, tempLastname)
        assertEquals(updated.user.username, tempUsername)
        assertEquals(updated.user.id, currentUserId)

    }

    @Order(4)
    @Test
    fun `test getting all user tasks`() {
        given()
            .`when`()
            .get("/api/v1/task")
            .then()
            .statusCode(UNAUTHORIZED)

        val tasks = given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .`when`()
            .get("/api/v1/task")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .extract()
            .`as`(getListOfTasksEntityTypeRef())

        tasks.forEach { task ->
            assertNotNull(task.id)
            assertEquals(task.user.firstname, tempFirstname)
            assertEquals(task.user.lastname, tempLastname)
            assertEquals(task.user.username, tempUsername)
            assertEquals(task.user.id, currentUserId)
        }
    }

    @Order(5)
    @Test
    fun `test deleting task`() {
        given()
            .pathParams("id", currentTaskId)
            .`when`()
            .delete("/api/v1/task/{id}")
            .then()
            .statusCode(UNAUTHORIZED)

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", dummyTaskId)
            .`when`()
            .delete("/api/v1/task/{id}")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$taskWith $dummyTaskId $idDoesNotExist"))

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentTaskId)
            .`when`()
            .delete("/api/v1/task/{id}")
            .then()
            .statusCode(OK)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$taskWith $currentTaskId $idSuccessfullyDeleted"))

        given()
            .auth()
            .preemptive()
            .basic(tempUsername, tempPassword)
            .pathParams("id", currentTaskId)
            .`when`()
            .delete("/api/v1/task/{id}")
            .then()
            .statusCode(BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body("message", `is`("$taskWith $currentTaskId $idDoesNotExist"))

        given()
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

    fun getListOfTasksEntityTypeRef() = object : TypeRef<List<TaskEntity>>() {}

    fun getTaskEntityTypeRef() = object : TypeRef<TaskEntity>() {}
}