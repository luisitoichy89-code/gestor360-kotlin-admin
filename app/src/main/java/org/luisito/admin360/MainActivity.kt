package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import org.luisito.admin360.ui.core.AppContent
import org.luisito.admin360.ui.theme.Gestor360Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Gestor360Theme {
                AppContent()
            }
        }
    }
}
