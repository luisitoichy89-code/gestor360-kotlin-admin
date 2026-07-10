package org.luisito.admin360.data.remote

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import org.luisito.admin360.BuildConfig

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        // OJO: estos Log.d solo deben imprimir en builds debug. En un release,
        // BuildConfig.DEBUG es false y esto queda mudo, así que la URL y el
        // prefijo de la key nunca llegan al Logcat de un APK publicado.
        if (BuildConfig.DEBUG) {
            Log.d("SUPABASE", "Inicializando cliente...")
            Log.d("SUPABASE", "URL: '${BuildConfig.SUPABASE_URL}'")
            Log.d("SUPABASE", "KEY empieza con: '${BuildConfig.SUPABASE_KEY.take(10)}...'")
            Log.d("SUPABASE", "KEY longitud: ${BuildConfig.SUPABASE_KEY.length}")
        }

        require(BuildConfig.SUPABASE_URL.isNotBlank()) {
            "SUPABASE_URL está vacío. Revisa que local.properties tenga la variable " +
                "y que build.gradle.kts la esté leyendo con Properties()/FileInputStream, " +
                "no con project.findProperty()."
        }
        require(BuildConfig.SUPABASE_KEY.isNotBlank()) {
            "SUPABASE_ANON_KEY está vacío. Debe ser la key 'anon public', nunca la 'service_role'."
        }

        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
