package org.luisito.admin360

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.data.repository.*
import org.luisito.admin360.data.models.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Gestor360Theme { AppContent() } }
    }
}

@Composable
fun AppContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        AdminDashboard()
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val authRepo = AuthRepository()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("🔐 Gestor360 Admin", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Acceso exclusivo para administradores", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            if (error!= null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        error = null
                        CoroutineScope(Dispatchers.Main).launch {
                            val result = authRepo.login(email, password)
                            isLoading = false
                            when (result) {
                                is LoginResult.Success -> onLoginSuccess()
                                is LoginResult.Error -> error = result.message
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =!isLoading,
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("Iniciar Sesión")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("negocios") }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogFields by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var onDialogConfirm by remember { mutableStateOf<((Map<String, String>) -> Unit)?>(null) }
    val negocioRepo = NegocioRepository()
    val localRepo = LocalRepository()
    val userRepo = AdminUserRepository()
    val licenciaRepo = LicenciaRepository()
    var negocios by remember { mutableStateOf<List<Negocio>>(emptyList()) }
    var locales by remember { mutableStateOf<List<Local>>(emptyList()) }
    var usuarios by remember { mutableStateOf<List<AdminUser>>(emptyList()) }
    var licencias by remember { mutableStateOf<List<Licencia>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadNegocios() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true
            try {
                negocios = negocioRepo.getNegocios()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ ${negocios.size} negocios cargados", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                    Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun loadLocales() {
        if (selectedNegocioId!= null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    locales = localRepo.getLocales(selectedNegocioId!!)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ ${locales.size} locales cargados", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun loadUsuarios() {
        if (selectedNegocioId!= null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    usuarios = userRepo.getUsers(selectedNegocioId!!)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ ${usuarios.size} usuarios cargados", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun loadLicencias() {
        if (selectedNegocioId!= null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    licencias = licenciaRepo.getLicencias(selectedNegocioId!!)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ ${licencias.size} licencias cargadas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadNegocios() }
    LaunchedEffect(selectedNegocioId) {
        if (selectedNegocioId!= null) {
            loadLocales()
            loadUsuarios()
            loadLicencias()
        }
    }

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
                                else -> {
                                    selectedItem = item.lowercase()
                                    if (item == "Negocios") loadNegocios()
                                }
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    when (selectedItem) {
                        "negocios" -> {
                            dialogTitle = "Crear Negocio"
                            dialogFields = listOf("Nombre" to "")
                            onDialogConfirm = { values ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val success = negocioRepo.createNegocio(values["Nombre"]?: "")
                                        withContext(Dispatchers.Main) {
                                            if (success) {
                                                Toast.makeText(context, "✅ Negocio creado correctamente", Toast.LENGTH_LONG).show()
                                                loadNegocios()
                                            } else {
                                                Toast.makeText(context, "❌ Error: ${ErrorHolder.lastError}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "❌ Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                            showDialog = true
                        }
                        "locales" -> {
                            dialogTitle = "Crear Local"
                            dialogFields = listOf("Nombre" to "")
                            onDialogConfirm = { values ->
                                if (selectedNegocioId!= null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val success = localRepo.createLocal(selectedNegocioId!!, values["Nombre"]?: "")
                                            withContext(Dispatchers.Main) {
                                                if (success) {
                                                    Toast.makeText(context, "✅ Local creado", Toast.LENGTH_SHORT).show()
                                                    loadLocales()
                                                } else {
                                                    Toast.makeText(context, "❌ Error al crear local", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "❌ Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            showDialog = true
                        }
                        "usuarios" -> {
                            dialogTitle = "Crear Usuario"
                            dialogFields = listOf("Usuario" to "", "Contraseña" to "", "Nombre" to "", "Rol" to "", "Local ID" to "")
                            onDialogConfirm = { values ->
                                if (selectedNegocioId!= null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val success = userRepo.createUser(
                                                selectedNegocioId!!,
                                                values["Usuario"]?: "",
                                                values["Contraseña"]?: "",
                                                values["Nombre"]?: "",
                                                values["Rol"]?: "seller",
                                                values["Local ID"]?: "1"
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (success) {
                                                    Toast.makeText(context, "✅ Usuario creado", Toast.LENGTH_SHORT).show()
                                                    loadUsuarios()
                                                } else {
                                                    Toast.makeText(context, "❌ Error al crear usuario", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "❌ Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            showDialog = true
                        }
                        "licencias" -> {
                            dialogTitle = "Crear Licencia"
                            dialogFields = listOf("Device ID" to "", "Días" to "30")
                            onDialogConfirm = { values ->
                                if (selectedNegocioId!= null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val success = licenciaRepo.activateLicense(
                                                selectedNegocioId!!,
                                                values["Device ID"]?: "",
                                                values["Días"]?.toIntOrNull()?: 30
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (success) {
                                                    Toast.makeText(context, "✅ Licencia creada", Toast.LENGTH_SHORT).show()
                                                    loadLicencias()
                                                } else {
                                                    Toast.makeText(context, "❌ Error al crear licencia", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "❌ Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            showDialog = true
                        }
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (errorMessage!= null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                } else {
                    when (selectedItem) {
                        "negocios" -> {
                            Text("Negocios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                            LazyColumn {
                                items(negocios) { negocio ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(negocio.nombre_negocio)
                                            Text(if (negocio.activo) "🟢 Activo" else "🔴 Inactivo")
    
                                        }
                                    }
                                }
                            }
                        }
                        "locales" -> {
                            Text("Locales", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                            LazyColumn {
                                items(locales) { local ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(local.nombre)
                                            Text(if (negocio.activo) "🟢 Activo" else "🔴 Inactivo")
                                        }
                                    }
                                }
                            }
                        }
                        "locales" -> {
                            Text("Locales", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                            LazyColumn {
                                items(locales) { local ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(local.nombre)
                                            Text(if (local.activo) "🟢 Activo" else "🔴 Inactivo")
                                        }
                                    }
                                }
                            }
                        }
                        "usuarios" -> {
                            Text("Usuarios", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                            LazyColumn {
                                items(usuarios) { usuario ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(usuario.username)
                                            Text(usuario.rol)
                                        }
                                    }
                                }
                            }
                        }
                        "licencias" -> {
                            Text("Licencias", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                            LazyColumn {
                                items(licencias) { licencia ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(licencia.device_id.take(12))
                                            Text(licencia.expiracion ?: "Sin fecha")
                                        }
                                    }
                                }
                            }
                        }
                        else -> Text("Selecciona una opción", modifier = Modifier.padding(16.dp))
                    }
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
                            onValueChange = { newValue ->
                                values = values.toMutableMap().apply { put(field.first, newValue) }
                            },
                            label = { Text(field.first) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDialogConfirm?.invoke(values)
                        showDialog = false
                    }
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
