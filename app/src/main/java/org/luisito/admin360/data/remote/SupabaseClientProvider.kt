package org.luisito.admin360.data.remote

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import org.luisito.admin360.BuildConfig

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        Log.d("SUPABASE", "Inicializando cliente...")
        Log.d("SUPABASE", "URL: '${BuildConfig.SUPABASE_URL}'")
        Log.d("SUPABASE", "KEY empieza con: '${BuildConfig.SUPABASE_ANON_KEY.take(20)}...'")
        Log.d("SUPABASE", "KEY longitud: ${BuildConfig.SUPABASE_ANON_KEY.length}")
        
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            Log.d("SUPABASE", "Instalando plugins...")
            install(Auth)
            install(Postgrest)
            Log.d("SUPABASE", "Cliente listo")
        }
    }
}
