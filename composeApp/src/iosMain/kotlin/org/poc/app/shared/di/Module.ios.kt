package org.poc.app.shared.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.shared.business.database.getPortfolioDatabaseBuilder
import org.poc.app.shared.business.database.portfolio.PortfolioDatabase
import org.poc.app.shared.business.domain.Logger
import org.poc.app.shared.business.domain.AnalyticsLogger
import org.poc.app.shared.business.domain.IOSLogger
import org.poc.app.shared.business.domain.IOSAnalyticsLogger
import org.poc.app.shared.business.domain.AppConfig

actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()

    // Platform-specific logging
    single<Logger> { IOSLogger(get<AppConfig>()) }
    single<AnalyticsLogger> { IOSAnalyticsLogger(get<AppConfig>()) }
}