package org.luisito.admin360.domain.repository

import org.luisito.admin360.domain.result.Result

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUserRole(): Result<String>
    suspend fun isAuthenticated(): Boolean
}
