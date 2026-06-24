package org.luisito.admin360.utils

import android.content.Context
import android.widget.Toast

object ToastManager {
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun showSuccess(context: Context, message: String) {
        Toast.makeText(context, "✅ $message", Toast.LENGTH_SHORT).show()
    }

    fun showError(context: Context, message: String) {
        Toast.makeText(context, "❌ $message", Toast.LENGTH_SHORT).show()
    }
}
