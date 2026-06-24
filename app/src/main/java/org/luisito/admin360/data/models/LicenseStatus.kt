package org.luisito.admin360.data.models

sealed class LicenseStatus {
    object Pending : LicenseStatus()
    object Active : LicenseStatus()
    data class Expired(val date: String) : LicenseStatus()
    data class Error(val message: String) : LicenseStatus()
}
