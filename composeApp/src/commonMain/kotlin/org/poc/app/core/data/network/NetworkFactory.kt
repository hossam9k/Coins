package org.poc.app.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Network Factory
 * Creates and configures HTTP clients for different services
 */
class NetworkFactory(
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        },
) {
    private val clientCache = mutableMapOf<String, HttpClient>()

    /**
     * Get or create an HTTP client for a specific base URL
     */
    fun getClient(
        baseUrl: String = ApiConfig.baseUrl,
        enableLogging: Boolean = true,
    ): HttpClient =
        clientCache.getOrPut(baseUrl) {
            createClient(baseUrl, enableLogging)
        }

    /**
     * Create a new HTTP client with configuration
     */
    private fun createClient(
        baseUrl: String,
        enableLogging: Boolean,
    ): HttpClient =
        HttpClient {
            // Base configuration
            defaultRequest {
                // This is the CORRECT way to set base URL in Ktor
                url(baseUrl)

                ApiConfig.getHeaders().forEach { (key, value) ->
                    header(key, value)
                }
            }

            // JSON serialization
            install(ContentNegotiation) {
                json(this@NetworkFactory.json)
            }

            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = ApiConfig.Config.TIMEOUT_SECONDS * 1000
                connectTimeoutMillis = ApiConfig.Config.TIMEOUT_SECONDS * 1000
            }

            // Logging - Shows API calls in console
            if (enableLogging) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL // Shows headers, body, everything!
                    filter { request ->
                        request.url.host.contains("api")
                    }
                    sanitizeHeader { header ->
                        // Hide sensitive headers
                        header == "Authorization" || header == "X-Api-Key"
                    }
                }
            }

            // Retry policy
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = ApiConfig.Config.RETRY_COUNT)
                exponentialDelay()
            }
        }

    /**
     * Clean up clients
     */
    fun cleanup() {
        clientCache.values.forEach { it.close() }
        clientCache.clear()
    }
}
