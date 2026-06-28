package org.luisito.admin360.data.models

import org.luisito.admin360.data.models.User

sealed class LoginResult {
    data class Success(val userId: Int, val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
