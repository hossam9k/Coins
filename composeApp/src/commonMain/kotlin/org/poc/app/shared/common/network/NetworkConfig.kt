package org.poc.app.shared.common.network

/**
 * Network configuration for the application
 *
 * In production, these values should be provided through:
 * - Environment variables
 * - Build configuration
 * - Secure key management systems
 */
object NetworkConfig {
    // TODO: Move to BuildConfig or environment variables
    // This is a temporary solution for development
    const val DEFAULT_API_KEY = "YOUR_API_KEY_HERE"
    const val BASE_URL = "https://api.coinranking.com/v2/"

    /**
     * Get API key from secure source
     * Override this method to implement secure key retrieval
     */
    fun getApiKey(): String {
        // TODO: Implement secure key retrieval from:
        // - Environment variables: System.getenv("COINRANKING_API_KEY")
        // - Build configuration: BuildConfig.COINRANKING_API_KEY
        // - Secure storage: KeyStore, encrypted preferences, etc.

        return DEFAULT_API_KEY
    }
}