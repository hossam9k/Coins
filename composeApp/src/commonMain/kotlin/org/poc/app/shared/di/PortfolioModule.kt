package org.poc.app.shared.di

import androidx.room.RoomDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.shared.business.database.portfolio.PortfolioDatabase
import org.poc.app.shared.business.database.portfolio.getPortfolioDatabase
import org.poc.app.feature.portfolio.data.PortfolioRepositoryImpl
import org.poc.app.feature.portfolio.domain.GetAllPortfolioCoinsUseCase
import org.poc.app.feature.portfolio.domain.GetCashBalanceUseCase
import org.poc.app.feature.portfolio.domain.GetTotalBalanceUseCase
import org.poc.app.feature.portfolio.domain.InitializeBalanceUseCase
import org.poc.app.feature.portfolio.domain.PortfolioRepository
import org.poc.app.feature.portfolio.presentation.PortfolioViewModel

/**
 * Portfolio feature module
 * Contains all dependencies for portfolio/holdings management
 */
val portfolioModule = module {
    // Database
    single {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().userBalanceDao() }

    // Repository
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()

    // Use Cases
    singleOf(::GetAllPortfolioCoinsUseCase)
    singleOf(::GetTotalBalanceUseCase)
    singleOf(::GetCashBalanceUseCase)
    singleOf(::InitializeBalanceUseCase)

    // ViewModel
    viewModel { PortfolioViewModel(get(), get(), get(), get(), get(), get(), get()) }
}