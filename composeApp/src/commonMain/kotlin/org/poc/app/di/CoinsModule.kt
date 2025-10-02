package org.poc.app.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.poc.app.feature.coins.data.CoinsRemoteDataSource
import org.poc.app.feature.coins.data.CoinsRepositoryImpl
import org.poc.app.feature.coins.data.KtorCoinsRemoteDataSource
import org.poc.app.feature.coins.domain.CoinsRepository
import org.poc.app.feature.coins.domain.GetCoinDetailsUseCase
import org.poc.app.feature.coins.domain.GetCoinPriceHistoryUseCase
import org.poc.app.feature.coins.domain.GetCoinsListUseCase
import org.poc.app.feature.coins.presentation.CoinsListViewModel

/**
 * Coins/Market data feature module
 * Contains all dependencies for cryptocurrency market data
 */
val coinsModule =
    module {
        // Data Sources
        // This gets the HttpClient from CoreModule, which uses NetworkFactory!
        single<CoinsRemoteDataSource> {
            KtorCoinsRemoteDataSource(
                httpClient = get(), // This comes from NetworkFactory in CoreModule
            )
        }

        // Repository
        single<CoinsRepository> {
            CoinsRepositoryImpl(
                remoteDataSource = get(),
            )
        }

        // Use Cases
        singleOf(::GetCoinsListUseCase)
        singleOf(::GetCoinDetailsUseCase)
        singleOf(::GetCoinPriceHistoryUseCase)

        // ViewModel
        viewModel { CoinsListViewModel(get(), get(), get(), get(), get()) }
    }
