package org.poc.app.core.data.network

/**
 * API Configuration
 * Centralized configuration for all API endpoints
 */
object ApiConfig {
    // Environment selection
    private val environment = Environment.PRODUCTION

    // Base URLs for different environments
    enum class Environment(
        val baseUrl: String,
    ) {
        // Using Coinranking API (requires API key)
        PRODUCTION("https://api.coinranking.com/v2/"),
        STAGING("https://api.coinranking.com/v2/"),
        DEVELOPMENT("http://localhost:8080/api/"),
        MOCK("http://localhost:3000/api/"),
    }

    // Get current base URL
    val baseUrl: String
        get() = environment.baseUrl

    // API endpoints (relative paths)
    object Endpoints {
        // Coinranking endpoints
        const val ALL_COINS = "coins"
        const val COIN_DETAILS = "coin" // append /{uuid}
        const val GLOBAL_DATA = "stats"
        // Add more endpoints here
    }

    // API configuration
    object Config {
        const val TIMEOUT_SECONDS = 30L
        const val RETRY_COUNT = 3
        const val CACHE_SIZE_MB = 10L
    }

    // Headers
    fun getHeaders(): Map<String, String> =
        buildMap {
            put("Accept", "application/json")
            put("Content-Type", "application/json")
            // Coinranking API key (required)
            // Get a free key at: https://developers.coinranking.com/
            put("x-access-token", getApiKey())
        }

    // API Key configuration
    fun getApiKey(): String {
        // For testing, you can put your API key here
        // In production, this should come from secure storage
        return "coinranking13464c4aa755bf134065cbbe9a00a0ff150e8b23241f3ea1"
    }
}
