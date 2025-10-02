package org.poc.app.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Main Koin initialization
 * Combines all feature modules for dependency injection
 */
fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            // Core infrastructure (shared across features)
            coreModule,
            // Feature modules (domain-specific)
            portfolioModule,
            coinsModule,
            tradeModule,
            // Platform-specific dependencies
            platformModule,
        )
    }

/**
 * Platform-specific module
 * Implemented in androidMain/iosMain for platform dependencies
 */
expect val platformModule: Module
