package org.poc.app.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.core.data.database.getPortfolioDatabaseBuilder
import org.poc.app.core.data.database.PortfolioDatabase
import org.poc.app.core.domain.model.Logger
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DesktopLogger
import org.poc.app.core.domain.model.DesktopAnalyticsLogger
import org.poc.app.core.domain.model.AppConfig

actual val platformModule = module {
    single<HttpClientEngine> { CIO.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()

    // Platform-specific logging
    single<Logger> { DesktopLogger(get<AppConfig>(), logToFile = true) }
    single<AnalyticsLogger> { DesktopAnalyticsLogger(get<AppConfig>(), logToFile = true) }
}