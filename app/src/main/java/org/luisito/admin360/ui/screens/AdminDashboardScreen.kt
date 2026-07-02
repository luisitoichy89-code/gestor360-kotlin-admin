package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Super Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("negocios") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Negocios")
                Text("Gestiona todos los negocios del sistema")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("licencias") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Licencias")
                Text("Control global de licencias activas")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("usuarios") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Usuarios")
                Text("Administradores y vendedores")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigate("locales") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Locales")
                Text("Gestión por negocio")
            }
        }
    }
}
