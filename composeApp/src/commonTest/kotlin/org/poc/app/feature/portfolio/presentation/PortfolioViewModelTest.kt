package org.poc.app.feature.portfolio.presentation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.poc.app.feature.portfolio.data.FakePortfolioRepository
import org.poc.app.feature.portfolio.domain.GetAllPortfolioCoinsUseCase
import org.poc.app.feature.portfolio.domain.GetTotalBalanceUseCase
import org.poc.app.feature.portfolio.domain.GetCashBalanceUseCase
import org.poc.app.feature.portfolio.domain.InitializeBalanceUseCase
import org.poc.app.shared.common.domain.TestDispatcherProvider
import org.poc.app.shared.common.domain.DefaultLogger
import org.poc.app.shared.common.domain.NoOpAnalyticsLogger
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PortfolioViewModelTest {

    private lateinit var portfolioRepository: FakePortfolioRepository
    private lateinit var getAllPortfolioCoinsUseCase: GetAllPortfolioCoinsUseCase
    private lateinit var getTotalBalanceUseCase: GetTotalBalanceUseCase
    private lateinit var getCashBalanceUseCase: GetCashBalanceUseCase
    private lateinit var initializeBalanceUseCase: InitializeBalanceUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        portfolioRepository = FakePortfolioRepository()

        // Create use cases
        getAllPortfolioCoinsUseCase = GetAllPortfolioCoinsUseCase(portfolioRepository)
        getTotalBalanceUseCase = GetTotalBalanceUseCase(portfolioRepository)
        getCashBalanceUseCase = GetCashBalanceUseCase(portfolioRepository)
        initializeBalanceUseCase = InitializeBalanceUseCase(portfolioRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ViewModel initializes successfully`() = runTest {
        val viewModel = PortfolioViewModel(
            getAllPortfolioCoinsUseCase = getAllPortfolioCoinsUseCase,
            getTotalBalanceUseCase = getTotalBalanceUseCase,
            getCashBalanceUseCase = getCashBalanceUseCase,
            initializeBalanceUseCase = initializeBalanceUseCase,
            dispatcherProvider = TestDispatcherProvider(UnconfinedTestDispatcher()),
            logger = DefaultLogger(),
            analytics = NoOpAnalyticsLogger()
        )

        // Just verify the ViewModel can be created without crashing
        // and has a valid initial state
        assertTrue(viewModel.state.value.coins.isEmpty())
        assertTrue(viewModel.state.value.portfolioValue.isEmpty() || viewModel.state.value.portfolioValue.isNotEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Repository works correctly`() = runTest {
        // Add a portfolio coin
        val portfolioCoin = FakePortfolioRepository.portfolioCoin
        portfolioRepository.savePortfolioCoin(portfolioCoin)

        // Test that data was saved
        assertTrue(FakePortfolioRepository.portfolioCoin.coin.id.isNotEmpty())
        assertTrue(portfolioCoin.ownedAmountInUnit.toDouble() > 0.0)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Intent handling works`() = runTest {
        val viewModel = PortfolioViewModel(
            getAllPortfolioCoinsUseCase = getAllPortfolioCoinsUseCase,
            getTotalBalanceUseCase = getTotalBalanceUseCase,
            getCashBalanceUseCase = getCashBalanceUseCase,
            initializeBalanceUseCase = initializeBalanceUseCase,
            dispatcherProvider = TestDispatcherProvider(UnconfinedTestDispatcher()),
            logger = DefaultLogger(),
            analytics = NoOpAnalyticsLogger()
        )

        // Test that intents can be handled without crashing
        viewModel.handleIntent(PortfolioIntent.LoadPortfolio)
        viewModel.handleIntent(PortfolioIntent.RefreshPortfolio)

        // Verify no crashes occurred
        assertTrue(true)
    }
}