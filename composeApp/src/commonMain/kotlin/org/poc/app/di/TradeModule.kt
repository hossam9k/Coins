package org.poc.app.di

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.poc.app.feature.trade.domain.BuyCoinUseCase
import org.poc.app.feature.trade.domain.SellCoinUseCase
import org.poc.app.feature.trade.presentation.buy.BuyViewModel
import org.poc.app.feature.trade.presentation.sell.SellViewModel

/**
 * Trading feature module
 * Contains all dependencies for buying/selling functionality
 */
val tradeModule =
    module {
        // Use Cases
        singleOf(::BuyCoinUseCase)
        singleOf(::SellCoinUseCase)

        // ViewModels with parameters
        viewModel { (coinId: String) ->
            BuyViewModel(get(), get(), get(), coinId, get(), get(), get())
        }
        viewModel { (coinId: String) ->
            SellViewModel(get(), get(), get(), coinId, get(), get(), get())
        }
    }
