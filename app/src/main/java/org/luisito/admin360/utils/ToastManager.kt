package org.luisito.admin360.utils

import android.content.Context
import android.widget.Toast

object ToastManager {
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
