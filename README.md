cd ~/gestor360-kotlin-admin

cat > README.md << 'EOF'
# Gestor360 Kotlin Admin

App de administración para Gestor360 construida con Kotlin + Jetpack Compose.

### **Funcionalidades**
- CRUD de Negocios: crear, actualizar, eliminar, activar/desactivar
- Gestión de Inventario por negocio
- Reportes de ventas y movimientos
- Login con Firebase Auth
- Arquitectura MVVM + StateFlow

### **Stack**
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Arquitectura**: MVVM, Repository Pattern
- **DI**: Hilt
- **Backend**: Firebase Firestore, Firebase Auth
- **Build**: Gradle 8.4 + AGP 8.2

### **Compilar desde PC**
1. Clona el repo:
   ```bash
   git clone https://github.com/tu-usuario/gestor360-kotlin-admin.git
