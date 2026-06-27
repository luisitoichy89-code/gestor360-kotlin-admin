package org.luisito.admin360.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.luisito.admin360.BuildConfig

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                }
            )
            install(Auth)
            install(Postgrest)
        }
    }
}
