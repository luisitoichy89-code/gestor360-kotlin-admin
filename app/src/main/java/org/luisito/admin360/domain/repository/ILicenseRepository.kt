package org.luisito.admin360.domain.repository

import org.luisito.admin360.domain.result.Result
import org.luisito.admin360.data.models.License

interface ILicenseRepository {
    suspend fun verifyLicense(androidId: String): Result<License>
    suspend fun activateLicense(androidId: String): Result<Unit>
    suspend fun getLicenseStatus(): Result<License>
}
