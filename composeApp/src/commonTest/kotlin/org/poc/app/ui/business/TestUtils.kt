package org.poc.app.ui.business

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger

/**
 * Test dispatcher provider that uses test dispatchers for all coroutine operations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}

/**
 * Test logger that captures log messages for verification
 */
class TestLogger : Logger {
    val debugMessages = mutableListOf<LogMessage>()
    val infoMessages = mutableListOf<LogMessage>()
    val warnMessages = mutableListOf<LogMessage>()
    val errorMessages = mutableListOf<LogMessage>()

    data class LogMessage(
        val tag: String,
        val message: String,
        val throwable: Throwable? = null,
    )

    override fun debug(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        debugMessages.add(LogMessage(tag, message, throwable))
    }

    override fun info(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        infoMessages.add(LogMessage(tag, message, throwable))
    }

    override fun warn(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        warnMessages.add(LogMessage(tag, message, throwable))
    }

    override fun error(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        errorMessages.add(LogMessage(tag, message, throwable))
    }

    fun clear() {
        debugMessages.clear()
        infoMessages.clear()
        warnMessages.clear()
        errorMessages.clear()
    }
}

/**
 * Test analytics logger that captures events for verification
 */
class TestAnalyticsLogger : AnalyticsLogger {
    val events = mutableListOf<AnalyticsEvent>()
    val errors = mutableListOf<AnalyticsError>()
    private var _userId: String? = null
    val userProperties = mutableMapOf<String, String>()

    val userId: String? get() = _userId

    data class AnalyticsEvent(
        val eventName: String,
        val parameters: Map<String, Any>,
    )

    data class AnalyticsError(
        val error: Throwable,
        val additionalData: Map<String, Any>,
    )

    override fun logEvent(
        eventName: String,
        parameters: Map<String, Any>,
    ) {
        events.add(AnalyticsEvent(eventName, parameters))
    }

    override fun logError(
        error: Throwable,
        additionalData: Map<String, Any>,
    ) {
        errors.add(AnalyticsError(error, additionalData))
    }

    override fun setUserId(userId: String) {
        this._userId = userId
    }

    override fun setUserProperty(
        key: String,
        value: String,
    ) {
        userProperties[key] = value
    }

    fun clear() {
        events.clear()
        errors.clear()
        _userId = null
        userProperties.clear()
    }
}

/**
 * Test utilities for setting up test dispatchers
 */
@OptIn(ExperimentalCoroutinesApi::class)
object TestDispatchers {
    fun setup(testDispatcher: TestDispatcher) {
        Dispatchers.setMain(testDispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }
}
