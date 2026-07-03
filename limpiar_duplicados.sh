#!/data/data/com.termux/files/usr/bin/bash
set -e

BASE="app/src/main/java/org/luisito/admin360"

if [ ! -d "$BASE" ]; then
  echo "No encuentro $BASE — ejecuta este script desde la raíz del repo."
  exit 1
fi

declare -a MAPA=(
  "Negocio.kt:data/models"
  "Local.kt:data/models"
  "Licencia.kt:data/models"
  "User.kt:data/models"
  "NegocioRepository.kt:data/repository"
  "LocalRepository.kt:data/repository"
  "LicenciaRepository.kt:data/repository"
  "UsuarioRepository.kt:data/repository"
  "SupabaseClientProvider.kt:data/remote"
  "NegocioViewModel.kt:ui/viewmodels"
  "LocalViewModel.kt:ui/viewmodels"
  "LicenciaViewModel.kt:ui/viewmodels"
  "UsuarioViewModel.kt:ui/viewmodels"
  "NegociosScreen.kt:ui/screens"
  "LocalesScreen.kt:ui/screens"
  "LicenciasScreen.kt:ui/screens"
  "UsuariosScreen.kt:ui/screens"
  "AdminDashboardScreen.kt:ui/screens"
  "CommonUi.kt:ui/components"
  "AppContent.kt:ui/core"
)

echo "=== Buscando y eliminando duplicados ==="
for par in "${MAPA[@]}"; do
  archivo="${par%%:*}"
  carpeta_correcta="${par##*:}"
  ruta_correcta="$BASE/$carpeta_correcta/$archivo"

  encontrados=$(find "$BASE" -type f -name "$archivo")

  if [ -z "$encontrados" ]; then
    echo "  (no se encontró $archivo, sáltalo)"
    continue
  fi

  if [ ! -f "$ruta_correcta" ]; then
    primera=$(echo "$encontrados" | head -n1)
    mkdir -p "$BASE/$carpeta_correcta"
    echo "  -> $archivo no estaba en $carpeta_correcta, moviendo desde $primera"
    mv "$primera" "$ruta_correcta"
    encontrados=$(find "$BASE" -type f -name "$archivo")
  fi

  while IFS= read -r ruta; do
    if [ "$ruta" != "$ruta_correcta" ]; then
      echo "  eliminando duplicado: $ruta"
      rm "$ruta"
    fi
  done <<< "$encontrados"
done

echo ""
echo "=== Verificación final ==="
for par in "${MAPA[@]}"; do
  archivo="${par%%:*}"
  count=$(find "$BASE" -type f -name "$archivo" | wc -l)
  echo "  $archivo: $count copia(s)"
done

echo ""
echo "Listo."
