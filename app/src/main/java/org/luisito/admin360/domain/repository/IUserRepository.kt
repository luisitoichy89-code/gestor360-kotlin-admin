package org.luisito.admin360.domain.repository

import org.luisito.admin360.domain.result.Result
import org.luisito.admin360.data.models.User

interface IUserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUserById(id: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
}
