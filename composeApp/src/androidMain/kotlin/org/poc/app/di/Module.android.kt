package org.poc.app.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.core.data.database.PortfolioDatabase
import org.poc.app.core.data.database.getPortfolioDatabaseBuilder
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.AndroidAnalyticsLogger
import org.poc.app.core.domain.model.AndroidLogger
import org.poc.app.core.domain.model.AppConfig
import org.poc.app.core.domain.model.Logger

actual val platformModule =
    module {
        // Core
        single<HttpClientEngine> { Android.create() }
        singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()

        // Platform-specific logging
        single<Logger> { AndroidLogger(get<AppConfig>()) }
        single<AnalyticsLogger> { AndroidAnalyticsLogger(get<AppConfig>()) }
    }
