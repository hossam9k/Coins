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
import kotlin.test.Test

class BuyScreenTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkSubmitButtonLabelChangesWithTradeType() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                iconUrl = "url",
                price = 50000.0
            )
        )

        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {}
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
                onSubmitClicked = {}
            )
        }

        // For SELL type, only "Sell Now" should be displayed
        onNodeWithText("Sell Now").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkIfCoinNameShowProperlyInBuy() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                iconUrl = "url",
                price = 50000.0
            )
        )

        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {}
            )
        }

        onNodeWithTag("trade_screen_coin_name").assertIsDisplayed()
        onNodeWithTag("trade_screen_coin_name").assertTextEquals("Bitcoin")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkErrorIsShownProperly() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                iconUrl = "url",
                price = 50000.0
            ),
            error = Res.string.error_unknown
        )

        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {}
            )
        }

        onNodeWithTag("trade_error").assertIsDisplayed()
    }
}