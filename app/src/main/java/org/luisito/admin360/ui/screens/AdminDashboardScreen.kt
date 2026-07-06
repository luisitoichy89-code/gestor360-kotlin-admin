package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.luisito.admin360.data.models.Negocio

private data class SeccionDashboard(
    val titulo: String, val descripcion: String, val icono: ImageVector, val ruta: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(negocioActivo: Negocio? = null, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    val secciones = buildList {
        add(SeccionDashboard("Negocios", "Gestiona todos los negocios", Icons.Default.Business, "negocios"))
        add(SeccionDashboard("Tickets", "Soporte a clientes", Icons.Default.HeadsetMic, "tickets"))
        add(SeccionDashboard("Almacenamiento", "Espacio usado por cada negocio", Icons.Default.Storage, "almacenamiento"))
        if (negocioActivo != null) add(SeccionDashboard("Gestionar ${negocioActivo.nombre_negocio}", "Locales · Usuarios · Licencia", Icons.Default.Settings, "gestionar"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text("GESTOR360°", fontWeight = FontWeight.Bold); Text("Super Admin Panel", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
                navigationIcon = { IconButton(onClick = onLogout) { Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión") } },
                actions = { if (negocioActivo != null) { Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer) { Text(negocioActivo.nombre_negocio, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium) } } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (secciones.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay secciones disponibles") }
            else LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxSize()) {
                items(secciones) { seccion -> DashboardCard(seccion) { onNavigate(seccion.ruta) } }
            }
        }
    }
}

@Composable
private fun DashboardCard(seccion: SeccionDashboard, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick, shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = seccion.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Column { Text(seccion.titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Spacer(modifier = Modifier.height(4.dp)); Text(seccion.descripcion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
