package org.poc.app.shared.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.poc.app.shared.business.domain.AppConfig
import org.poc.app.shared.business.domain.DefaultAppConfig
import org.poc.app.shared.business.domain.DefaultDispatcherProvider
import org.poc.app.shared.business.domain.DispatcherProvider
import org.poc.app.shared.business.network.HttpClientFactory
import org.poc.app.shared.business.network.NetworkConfig

/**
 * Core infrastructure module
 * Contains shared dependencies used across the entire app
 */
val coreModule = module {
    // Application Configuration
    single<AppConfig> { DefaultAppConfig() }
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // Network
    single<HttpClient> {
        HttpClientFactory.create(
            engine = get(),
            apiKey = NetworkConfig.getApiKey(),
            isDebug = false // TODO: Use BuildConfig.DEBUG or similar
        )
    }
}