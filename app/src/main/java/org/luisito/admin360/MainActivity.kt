package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.theme.Admin360Theme
import org.luisito.admin360.ui.viewmodels.LocalViewModel
import org.luisito.admin360.ui.viewmodels.NegocioViewModel

class MainActivity : ComponentActivity() {
    private val negocioViewModel: NegocioViewModel by viewModels()
    private val localViewModel: LocalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Admin360Theme {
                var currentScreen by remember { mutableStateOf("main") }
                
                when (currentScreen) {
                    "main" -> MainScreen(
                        negocioViewModel = negocioViewModel,
                        localViewModel = localViewModel,
                        onNavigateToNegocios = { currentScreen = "negocios" }
                    )
                    "negocios" -> NegociosScreen(
                        viewModel = negocioViewModel,
                        onNavigateBack = { currentScreen = "main" }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    negocioViewModel: NegocioViewModel,
    localViewModel: LocalViewModel,
    onNavigateToNegocios: () -> Unit
) {
    val negocioUiState by negocioViewModel.uiState.collectAsState()
    val localUiState by localViewModel.uiState.collectAsState()
    val negocios = negocioUiState.negocios
    val locales = localUiState.locales
    val selectedNegocio = localUiState.selectedNegocio

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏢 Admin360") },
                actions = {
                    IconButton(onClick = onNavigateToNegocios) {
                        Icon(Icons.Default.Business, contentDescription = "Negocios")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Negocios", style = MaterialTheme.typography.titleLarge)
            LazyColumn {
                items(negocios) { negocio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        onClick = { localViewModel.selectNegocio(negocio.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(negocio.nombre_negocio, style = MaterialTheme.typography.titleMedium)
                            Text("ID: ${negocio.id}", style = MaterialTheme.typography.bodySmall)
                            Text(if (negocio.activo) "🟢 Activo" else "🔴 Inactivo")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Locales", style = MaterialTheme.typography.titleLarge)
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
    }
}
