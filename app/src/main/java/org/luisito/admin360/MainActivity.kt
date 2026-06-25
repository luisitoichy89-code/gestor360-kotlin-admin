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
import org.luisito.admin360.data.repository.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("negocios") }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogFields by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var onDialogConfirm by remember { mutableStateOf<((Map<String, String>) -> Unit)?>(null) }

    val negocioRepo = NegocioRepository()
    val localRepo = LocalRepository()
    val userRepo = AdminUserRepository()
    val licenciaRepo = LicenciaRepository()

    var negocios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var locales by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var usuarios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var licencias by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    fun loadNegocios() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = negocioRepo.getNegocios()
            negocios = result
        }
    }

    fun loadLocales() {
        if (selectedNegocioId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = localRepo.getLocales(selectedNegocioId!!)
                locales = result
            }
        }
    }

    fun loadUsuarios() {
        if (selectedNegocioId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = userRepo.getUsers(selectedNegocioId!!)
                usuarios = result
            }
        }
    }

    fun loadLicencias() {
        if (selectedNegocioId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = licenciaRepo.getLicencias(selectedNegocioId!!)
                licencias = result
            }
        }
    }

    LaunchedEffect(Unit) { loadNegocios() }
    LaunchedEffect(selectedNegocioId) { loadLocales(); loadUsuarios(); loadLicencias() }

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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    when (selectedItem) {
                        "negocios" -> { dialogTitle = "Crear Negocio"; dialogFields = listOf("Nombre" to ""); onDialogConfirm = { values -> 
                            CoroutineScope(Dispatchers.IO).launch {
                                negocioRepo.createNegocio(values["Nombre"] ?: "")
                                loadNegocios()
                            }
                        }; showDialog = true }
                        "locales" -> { dialogTitle = "Crear Local"; dialogFields = listOf("Nombre" to ""); onDialogConfirm = { values ->
                            if (selectedNegocioId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    localRepo.createLocal(selectedNegocioId!!, values["Nombre"] ?: "")
                                    loadLocales()
                                }
                            }
                        }; showDialog = true }
                        "usuarios" -> { dialogTitle = "Crear Usuario"; dialogFields = listOf("Usuario" to "", "Contraseña" to "", "Nombre" to "", "Rol" to "", "Local ID" to ""); onDialogConfirm = { values ->
                            if (selectedNegocioId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    userRepo.createUser(selectedNegocioId!!, values["Usuario"] ?: "", values["Contraseña"] ?: "", values["Nombre"] ?: "", values["Rol"] ?: "seller", values["Local ID"] ?: "1")
                                    loadUsuarios()
                                }
                            }
                        }; showDialog = true }
                        "licencias" -> { dialogTitle = "Crear Licencia"; dialogFields = listOf("Device ID" to "", "Días" to "30"); onDialogConfirm = { values ->
                            if (selectedNegocioId != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    licenciaRepo.createLicencia(selectedNegocioId!!, values["Device ID"] ?: "", values["Días"]?.toIntOrNull() ?: 30)
                                    loadLicencias()
                                }
                            }
                        }; showDialog = true }
                    }
                }) { Icon(Icons.Default.Menu, contentDescription = "Crear") }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                when (selectedItem) {
                    "negocios" -> {
                        Text("Negocios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        LazyColumn { items(negocios) { negocio -> Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text(negocio["nombre_negocio"]?.toString() ?: "Sin nombre", modifier = Modifier.padding(16.dp)) } } }
                    }
                    "locales" -> {
                        Text("Locales", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        LazyColumn { items(locales) { local -> Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text(local["nombre"]?.toString() ?: "Sin nombre", modifier = Modifier.padding(16.dp)) } } }
                    }
                    "usuarios" -> {
                        Text("Usuarios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        LazyColumn { items(usuarios) { usuario -> Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text(usuario["username"]?.toString() ?: "Sin usuario", modifier = Modifier.padding(16.dp)) } } }
                    }
                    "licencias" -> {
                        Text("Licencias", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                        LazyColumn { items(licencias) { licencia -> Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) { Text(licencia["device_id"]?.toString() ?: "Sin ID", modifier = Modifier.padding(16.dp)) } } }
                    }
                    else -> Text("Selecciona una opción", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    if (showDialog) {
        var values by remember { mutableStateOf(dialogFields.associate { it.first to "" }) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = {
                Column {
                    dialogFields.forEach { field ->
                        OutlinedTextField(
                            value = values[field.first] ?: "",
                            onValueChange = { values = values + (field.first to it) },
                            label = { Text(field.first) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onDialogConfirm?.invoke(values)
                    showDialog = false
                }) { Text("Crear") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }
}
