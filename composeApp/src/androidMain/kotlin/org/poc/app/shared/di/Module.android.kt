package org.poc.app.shared.di

import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.shared.common.database.getPortfolioDatabaseBuilder
import org.poc.app.shared.common.database.portfolio.PortfolioDatabase
import org.poc.app.shared.common.domain.Logger
import org.poc.app.shared.common.domain.AnalyticsLogger
import org.poc.app.shared.common.domain.AndroidLogger
import org.poc.app.shared.common.domain.AndroidAnalyticsLogger
import org.poc.app.shared.common.domain.AppConfig

actual val platformModule = module {
    //Core
    single<HttpClientEngine> { Android.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()

    // Platform-specific logging
    single<Logger> { AndroidLogger(get<AppConfig>()) }
    single<AnalyticsLogger> { AndroidAnalyticsLogger(get<AppConfig>()) }
}
