package org.poc.app.shared.di

import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.poc.app.shared.common.domain.AppConfig
import org.poc.app.shared.common.domain.DefaultAppConfig
import org.poc.app.shared.common.domain.DefaultDispatcherProvider
import org.poc.app.shared.common.domain.DispatcherProvider
import org.poc.app.shared.common.network.HttpClientFactory
import org.poc.app.shared.common.network.NetworkConfig

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