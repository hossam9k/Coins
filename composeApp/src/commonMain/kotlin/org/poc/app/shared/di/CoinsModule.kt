package org.poc.app.shared.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.poc.app.feature.coins.data.CoinsRepositoryImpl
import org.poc.app.feature.coins.data.remote.api.CoinsRemoteDataSource
import org.poc.app.feature.coins.data.remote.impl.KtorCoinsRemoteDataSource
import org.poc.app.feature.coins.domain.CoinsRepository
import org.poc.app.feature.coins.domain.GetCoinDetailsUseCase
import org.poc.app.feature.coins.domain.GetCoinPriceHistoryUseCase
import org.poc.app.feature.coins.domain.GetCoinsListUseCase
import org.poc.app.feature.coins.presentation.CoinsListViewModel

/**
 * Coins/Market data feature module
 * Contains all dependencies for cryptocurrency market data
 */
val coinsModule = module {
    // Data Sources
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()

    // Repository
    singleOf(::CoinsRepositoryImpl).bind<CoinsRepository>()

    // Use Cases
    singleOf(::GetCoinsListUseCase)
    singleOf(::GetCoinDetailsUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)

    // ViewModel
    viewModel { CoinsListViewModel(get(), get(), get(), get(), get()) }
}