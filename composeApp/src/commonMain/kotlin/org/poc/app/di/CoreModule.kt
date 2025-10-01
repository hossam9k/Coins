package org.poc.app.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.poc.app.core.domain.model.AppConfig
import org.poc.app.core.domain.model.DevAppConfig
import org.poc.app.core.domain.model.DefaultDispatcherProvider
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.data.network.NetworkFactory
import org.poc.app.core.data.network.ApiConfig

/**
 * Core infrastructure module
 * Contains shared dependencies used across the entire app
 *
 * Network Flow:
 * 1. NetworkFactory creates configured HTTP clients
 * 2. ApiConfig provides base URLs and endpoints
 * 3. Features inject this HttpClient to make API calls
 */
val coreModule = module {
    // Application Configuration
    // Use DevAppConfig for debugging with logging enabled
    // Change to ProdAppConfig("your-api-key") for production
    single<AppConfig> { DevAppConfig() } // ← Enables logging!
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // STEP 1: Create NetworkFactory instance
    single { NetworkFactory() }

    // STEP 2: Create HTTP Client using NetworkFactory
    // This client is pre-configured with:
    // - Base URL from ApiConfig
    // - Timeout, retry, logging settings
    // - JSON serialization
    single<HttpClient> {
        get<NetworkFactory>().getClient(
            baseUrl = ApiConfig.baseUrl,  // Uses ApiConfig for base URL
            enableLogging = get<AppConfig>().isDebug
        )
    }

    // STEP 3: Features will inject this HttpClient
    // Example: KtorCoinsRemoteDataSource(httpClient = get())
}