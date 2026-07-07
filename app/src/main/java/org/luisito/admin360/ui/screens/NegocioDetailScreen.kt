import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Negocio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioDetailScreen(
    negocio: Negocio,
    onBack: () -> Unit
) {
    val tabs = listOf("Locales", "Usuarios", "Licencia")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()
    val selectedTab = pagerState.currentPage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(negocio.nombre_negocio) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                actions = {
                    IconButton(onClick = { /* placeholder */ }) {
                        Icon(Icons.Default.Refresh, "Refrescar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(title) },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.Storefront, null, modifier = Modifier.size(18.dp))
                                1 -> Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp))
                                2 -> Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    )
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> LocalesScreen(
                        negocioId = negocio.id,
                        negocioNombre = negocio.nombre_negocio,
                        onBack = null
                    )
                    1 -> UsuariosScreen(
                        clienteId = negocio.id,
                        negocioNombre = negocio.nombre_negocio,
                        onBack = null
                    )
                    2 -> LicenciasScreen(
                        negocioId = negocio.id,
                        negocioNombre = negocio.nombre_negocio,
                        onBack = null
                    )
                }
            }
        }
    }
}
