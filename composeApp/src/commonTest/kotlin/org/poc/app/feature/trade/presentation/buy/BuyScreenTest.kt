package org.poc.app.feature.trade.presentation.buy

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.error_unknown
import org.poc.app.feature.trade.presentation.common.TradeScreen
import org.poc.app.feature.trade.presentation.common.TradeState
import org.poc.app.feature.trade.presentation.common.TradeType
import org.poc.app.feature.trade.presentation.common.UiTradeCoinItem
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * UI tests for TradeScreen.
 *
 * Note: These tests use runComposeUiTest which requires proper Compose testing setup.
 * In KMP projects, these should be run as instrumented tests (androidInstrumentedTest)
 * for proper resource initialization. Currently ignored until test infrastructure is set up.
 */
class BuyScreenTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    @Ignore("Requires instrumented test environment for Compose resources")
    fun checkSubmitButtonLabelChangesWithTradeType() =
        runComposeUiTest {
            val state =
                TradeState(
                    coin =
                        UiTradeCoinItem(
                            id = "bitcoin",
                            name = "Bitcoin",
                            symbol = "BTC",
                            iconUrl = "url",
                            price = 50000.0,
                        ),
                )

            setContent {
                TradeScreen(
                    state = state,
                    tradeType = TradeType.BUY,
                    onAmountChange = {},
                    onSubmitClicked = {},
                )
            }

            // For BUY type, only "Buy Now" should be displayed
            onNodeWithText("Buy Now").assertIsDisplayed()

            // Test SELL type
            setContent {
                TradeScreen(
                    state = state,
                    tradeType = TradeType.SELL,
                    onAmountChange = {},
                    onSubmitClicked = {},
                )
            }

            // For SELL type, only "Sell Now" should be displayed
            onNodeWithText("Sell Now").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    @Ignore("Requires instrumented test environment for Compose resources")
    fun checkIfCoinNameShowProperlyInBuy() =
        runComposeUiTest {
            val state =
                TradeState(
                    coin =
                        UiTradeCoinItem(
                            id = "bitcoin",
                            name = "Bitcoin",
                            symbol = "BTC",
                            iconUrl = "url",
                            price = 50000.0,
                        ),
                )

            setContent {
                TradeScreen(
                    state = state,
                    tradeType = TradeType.BUY,
                    onAmountChange = {},
                    onSubmitClicked = {},
                )
            }

            onNodeWithTag("trade_screen_coin_name").assertIsDisplayed()
            onNodeWithTag("trade_screen_coin_name").assertTextEquals("Bitcoin")
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    @Ignore("Requires instrumented test environment for Compose resources")
    fun checkErrorIsShownProperly() =
        runComposeUiTest {
            val state =
                TradeState(
                    coin =
                        UiTradeCoinItem(
                            id = "bitcoin",
                            name = "Bitcoin",
                            symbol = "BTC",
                            iconUrl = "url",
                            price = 50000.0,
                        ),
                    error = Res.string.error_unknown,
                )

            setContent {
                TradeScreen(
                    state = state,
                    tradeType = TradeType.BUY,
                    onAmountChange = {},
                    onSubmitClicked = {},
                )
            }

            onNodeWithTag("trade_error").assertIsDisplayed()
        }
}
