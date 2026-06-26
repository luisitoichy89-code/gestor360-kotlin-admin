package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.luisito.admin360.data.model.Local
import org.luisito.admin360.data.model.Negocio
import org.luisito.admin360.ui.screens.*
import org.luisito.admin360.ui.theme.Admin360Theme
import org.luisito.admin360.viewmodel.LocalViewModel
import org.luisito.admin360.viewmodel.NegocioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Admin360Theme {
                Admin360App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Admin360App() {
    val navController = rememberNavController()
    val negocioViewModel: NegocioViewModel = viewModel()
    val localViewModel: LocalViewModel = viewModel()
    
    val negocios by negocioViewModel.negocios.collectAsState()
    val locales by localViewModel.locales.collectAsState()
    val selectedNegocio by negocioViewModel.selectedNegocio.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin360") },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    label = { Text("Negocios") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Store, contentDescription = null) },
                    label = { Text("Locales") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    label = { Text("Usuarios") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { /* TODO: Add negocio */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar negocio")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    Text("Negocios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                    LazyColumn {
                        items(negocios) { negocio ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                onClick = { negocioViewModel.selectNegocio(negocio) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(negocio.nombre_negocio, style = MaterialTheme.typography.titleMedium)
                                        Text("ID: ${negocio.id}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(if (negocio.activo) "🟢 Activo" else "🔴 Inactivo")
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Text("Locales", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                    LazyColumn {
                        items(locales) { local ->
                            Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(local.nombre)
                                    Text(if (local.activo) "🟢 Activo" else "🔴 Inactivo")
                                }
                            }
                        }
                    }
                }
                2 -> {
                    Text("Usuarios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Módulo de usuarios en desarrollo")
                    }
                }
            }
        }
    }
}
