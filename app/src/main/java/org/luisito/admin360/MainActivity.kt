package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.data.repository.AuthRepository
import org.luisito.admin360.data.repository.LoginResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Gestor360Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (isLoggedIn) DashboardScreen() else LoginScreen()
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val authRepo = AuthRepository()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔐 Gestor360 Admin", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("Acceso exclusivo para administradores", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        if (error != null) { Spacer(Modifier.height(8.dp)); Text(text = error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                isLoading = true; error = null
                CoroutineScope(Dispatchers.Main).launch {
                    val result = authRepo.login(email, password)
                    isLoading = false
                    when (result) {
                        is LoginResult.Success -> { isLoggedIn = true; error = null }
                        is LoginResult.Error -> { error = result.message }
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text("Iniciar Sesión")
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun DashboardScreen() {
    val negocios = listOf(
        "Cafetería La Esquina" to "Activo",
        "Tienda El Centro" to "Inactivo",
        "Farmacia Salud" to "Activo",
        "Panadería El Trigal" to "Activo",
        "Librería El Saber" to "Inactivo"
    )
    val locales = listOf("Local 1", "Local 2", "Local 3")
    val usuarios = listOf("Admin", "Vendedor1", "Vendedor2")

    var selectedTab by remember { mutableStateOf("negocios") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📊 Panel de Control", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { selectedTab = "negocios" }) { Text("🏢 Negocios") }
            Button(onClick = { selectedTab = "locales" }) { Text("🏪 Locales") }
            Button(onClick = { selectedTab = "usuarios" }) { Text("👥 Usuarios") }
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            "negocios" -> {
                Text("Lista de Negocios", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(negocios) { (nombre, estado) ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(nombre, style = MaterialTheme.typography.titleSmall)
                                Text("Estado: $estado", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            "locales" -> {
                Text("Lista de Locales", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(locales) { local ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(local, style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
            "usuarios" -> {
                Text("Lista de Usuarios", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(usuarios) { usuario ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(usuario, style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { isLoggedIn = false }) { Text("🚪 Cerrar Sesión") }
    }
}
