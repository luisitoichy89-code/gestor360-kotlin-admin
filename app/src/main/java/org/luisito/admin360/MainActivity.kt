package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.data.repository.AuthRepository
import org.luisito.admin360.data.repository.LoginResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppContent() }
    }
}

@Composable
fun AppContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    if (!isLoggedIn) LoginScreen(onLoginSuccess = { isLoggedIn = true })
    else AdminDashboard()
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val authRepo = AuthRepository()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("🔐 Gestor360 Admin", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Acceso exclusivo para administradores", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
            if (error != null) { Spacer(Modifier.height(8.dp)); Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true; error = null
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = authRepo.login(email, password)
                        isLoading = false
                        when (result) {
                            is LoginResult.Success -> onLoginSuccess()
                            is LoginResult.Error -> error = result.message
                        }
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("Iniciar Sesión")
            }
        }
    }
}

@Composable
fun AdminDashboard() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("negocios") }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Gestor360 Admin", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                Divider()
                listOf("Negocios", "Locales", "Usuarios", "Licencias", "Cerrar Sesión").forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = selectedItem == item.lowercase(),
                        onClick = {
                            scope.launch { drawerState.close() }
                            when (item) {
                                "Cerrar Sesión" -> { /* logout */ }
                                else -> selectedItem = item.lowercase()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestor360 Admin") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (selectedItem) {
                    "negocios" -> NegociosList()
                    "locales" -> LocalesList()
                    "usuarios" -> UsuariosList()
                    "licencias" -> LicenciasList()
                    else -> Text("Selecciona una opción")
                }
            }
        }
    }
}

@Composable
fun NegociosList() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Negocios", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        val negocios = listOf("Cafetería La Esquina", "Tienda El Centro", "Farmacia Salud")
        LazyColumn {
            items(negocios) { negocio ->
                Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(negocio, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun LocalesList() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Locales", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        val locales = listOf("Local 1", "Local 2", "Local 3")
        LazyColumn {
            items(locales) { local ->
                Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(local, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun UsuariosList() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Usuarios", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        val usuarios = listOf("Admin", "Vendedor1", "Vendedor2")
        LazyColumn {
            items(usuarios) { usuario ->
                Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(usuario, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun LicenciasList() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Licencias", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        val licencias = listOf("Licencia 1", "Licencia 2", "Licencia 3")
        LazyColumn {
            items(licencias) { licencia ->
                Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Text(licencia, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
