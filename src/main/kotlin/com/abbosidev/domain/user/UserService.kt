package com.abbosidev.domain.user

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserService {

    fun saveNewUser(user: UserDto): Uni<UserEntity?> {
        return UserEntity.countUsers(user.username).flatMap { count ->
            if (count > 0) Uni.createFrom().nullItem()
            else UserEntity.save(user)
        }
    }

    fun login(username: String) = UserEntity.getUserByUsername(username)
}