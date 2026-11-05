package com.example.core.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.delay
import java.io.IOException

class ApiClient(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexus_api_prefs", Context.MODE_PRIVATE)

    var baseUrl: String
        get() = prefs.getString("api_base_url", "https://api.nexus-search.io/v1")
            ?: "https://api.nexus-search.io/v1"
        set(value) {
            prefs.edit().putString("api_base_url", value.trim()).apply()
        }

    var isLiveEndpointEnabled: Boolean
        get() = prefs.getBoolean("use_live_endpoint", false)
        set(value) {
            prefs.edit().putBoolean("use_live_endpoint", value).apply()
        }

    var authToken: String?
        get() = prefs.getString("jwt_auth_token", null)
        set(value) {
            prefs.edit().putString("jwt_auth_token", value).apply()
        }

    var refreshToken: String?
        get() = prefs.getString("jwt_refresh_token", null)
        set(value) {
            prefs.edit().putString("jwt_refresh_token", value).apply()
        }

    var simulateNetworkError: Boolean
        get() = prefs.getBoolean("sim_network_err", false)
        set(value) {
            prefs.edit().putBoolean("sim_network_err", value).apply()
        }

    var simulatedLatencyMs: Long
        get() = prefs.getLong("sim_latency_ms", 350L)
        set(value) {
            prefs.edit().putLong("sim_latency_ms", value).apply()
        }

    suspend fun executeWithInterceptors(
        endpoint: String,
        action: suspend () -> Any
    ): Result<Any> {
        val start = System.currentTimeMillis()
        if (simulateNetworkError) {
            delay(simulatedLatencyMs)
            return Result.failure(IOException("503 Service Temporarily Unavailable - Remote DuckDB Gateway Timeout"))
        }

        delay(simulatedLatencyMs)
        return try {
            val result = action()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearSession() {
        prefs.edit().remove("jwt_auth_token").remove("jwt_refresh_token").apply()
    }
}
