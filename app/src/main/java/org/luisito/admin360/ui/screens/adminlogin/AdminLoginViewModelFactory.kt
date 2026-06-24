package org.luisito.admin360.ui.screens.adminlogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.luisito.admin360.utils.DataStoreManager

class AdminLoginViewModelFactory(
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminLoginViewModel(dataStoreManager.context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
