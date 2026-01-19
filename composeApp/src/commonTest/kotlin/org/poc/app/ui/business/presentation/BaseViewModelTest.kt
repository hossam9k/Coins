package org.poc.app.core.presentation.base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.poc.app.ui.business.TestAnalyticsLogger
import org.poc.app.ui.business.TestDispatcherProvider
import org.poc.app.ui.business.TestLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider = TestDispatcherProvider(testDispatcher)
    private val testLogger = TestLogger()
    private val testAnalytics = TestAnalyticsLogger()

    private inner class TestViewModel : BaseViewModel(dispatcherProvider, testLogger, testAnalytics) {
        fun triggerError() {
            launchSafe {
                throw RuntimeException("Test error")
            }
        }

        fun logTestAction() {
            logUserAction("test_action", mapOf("param" to "value"))
        }

        fun logTestScreenView() {
            logScreenView("test_screen")
        }
    }

    @Test
    fun `launchSafe should handle exceptions and log errors`() =
        runTest(testDispatcher) {
            val viewModel = TestViewModel()

            viewModel.triggerError()

            assertTrue(testLogger.errorMessages.isNotEmpty())
            assertTrue(testAnalytics.errors.isNotEmpty())
        }

    @Test
    fun `logUserAction should log analytics event`() =
        runTest(testDispatcher) {
            val viewModel = TestViewModel()
            testAnalytics.clear() // Clear events from init block

            viewModel.logTestAction()

            assertEquals(1, testAnalytics.events.size)
            val event = testAnalytics.events.first()
            assertEquals("user_action", event.eventName)
            assertEquals("test_action", event.parameters["action"])
            assertEquals("TestViewModel", event.parameters["screen"])
        }

    @Test
    fun `logScreenView should log screen view event`() =
        runTest(testDispatcher) {
            val viewModel = TestViewModel()
            testAnalytics.clear() // Clear events from init block

            viewModel.logTestScreenView()

            assertEquals(1, testAnalytics.events.size)
            val event = testAnalytics.events.first()
            assertEquals("screen_view", event.eventName)
            assertEquals("test_screen", event.parameters["screen_name"])
        }
}
