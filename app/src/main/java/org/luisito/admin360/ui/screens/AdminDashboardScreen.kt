package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.luisito.admin360.data.models.Negocio

private data class SeccionDashboard(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val ruta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    negocioActivo: Negocio? = null,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val secciones = buildList {
        add(SeccionDashboard("Negocios", "Gestiona todos los negocios", Icons.Default.Business, "negocios"))
        if (negocioActivo != null) {
            add(SeccionDashboard(
                "Gestionar ${negocioActivo.nombre_negocio}",
                "Locales · Usuarios · Licencia",
                Icons.Default.Settings,
                "gestionar"
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Super Admin") },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Cerrar sesión")
                    }
                },
                actions = {
                    if (negocioActivo != null) {
                        Text(
                            text = negocioActivo.nombre_negocio,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (secciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay secciones disponibles")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(secciones) { seccion ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigate(seccion.ruta) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(seccion.icono, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(seccion.titulo, style = MaterialTheme.typography.titleSmall)
                            Text(seccion.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
