package org.poc.app.shared.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.shared.common.database.getPortfolioDatabaseBuilder
import org.poc.app.shared.common.database.portfolio.PortfolioDatabase
import org.poc.app.shared.common.domain.Logger
import org.poc.app.shared.common.domain.AnalyticsLogger
import org.poc.app.shared.common.domain.DesktopLogger
import org.poc.app.shared.common.domain.DesktopAnalyticsLogger
import org.poc.app.shared.common.domain.AppConfig

actual val platformModule = module {
    single<HttpClientEngine> { CIO.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()

    // Platform-specific logging
    single<Logger> { DesktopLogger(get<AppConfig>(), logToFile = true) }
    single<AnalyticsLogger> { DesktopAnalyticsLogger(get<AppConfig>(), logToFile = true) }
}