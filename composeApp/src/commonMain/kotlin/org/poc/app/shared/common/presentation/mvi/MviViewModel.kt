package org.poc.app.shared.common.presentation.mvi

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.TimeSource
import org.poc.app.shared.common.presentation.BaseViewModel
import org.poc.app.shared.common.domain.DispatcherProvider
import org.poc.app.shared.common.domain.Logger
import org.poc.app.shared.common.domain.AnalyticsLogger

/**
 * Production-ready MVI ViewModel extending BaseViewModel for comprehensive infrastructure
 *
 * Combines MVI pattern with BaseViewModel features:
 * - Thread-safe state updates with MVI pattern
 * - Safe coroutine management from BaseViewModel
 * - Comprehensive error handling and logging
 * - Intent deduplication for expensive operations
 * - Automatic analytics and performance tracking
 * - Side effect management with configurable buffer
 * - State history for debugging (optional)
 *
 * @param State - Must implement UiState for type safety
 * @param Intent - Must implement UiIntent for type safety
 * @param SideEffect - Must implement UiSideEffect for type safety
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, SideEffect : UiSideEffect>(
    initialState: State,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
    private val sideEffectBufferSize: Int = 64
) : BaseViewModel(dispatcherProvider, logger, analytics) {

    companion object {
        private const val TAG = "MviViewModel"
    }

    // Thread-safe state management
    private val stateMutex = Mutex()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected val currentState: State get() = _state.value

    // Side Effects with configurable buffer
    private val _sideEffect = Channel<SideEffect>(capacity = sideEffectBufferSize)
    val sideEffect = _sideEffect.receiveAsFlow()

    // Intent deduplication for expensive operations
    private val runningIntents = mutableSetOf<String>()

    // Performance tracking
    private val timeSource = TimeSource.Monotonic

    /**
     * Handle user intents with comprehensive error handling using BaseViewModel infrastructure
     */
    fun handleIntent(intent: Intent) {
        val intentKey = intent::class.simpleName ?: "UnknownIntent"

        // Use BaseViewModel's safe launching with operation tracking
        launchSafe(
            operationName = "handle_intent_$intentKey",
            preventDuplicates = shouldDeduplicateIntent(intent)
        ) {
            val startMark = timeSource.markNow()

            // Log user action through BaseViewModel
            logUserAction("intent", mapOf(
                "intentType" to intentKey,
                "intentData" to intent.toString()
            ))

            try {
                processIntent(intent)

                // Log performance metric through BaseViewModel
                val executionTime = startMark.elapsedNow().inWholeMilliseconds
                logPerformanceMetric("intent_$intentKey", executionTime)

            } catch (e: Exception) {
                // Use BaseViewModel's error handling + MVI-specific error handling
                logger.error(TAG, "Error processing intent: $intentKey", e)
                analytics.logError(e, mapOf(
                    "intentType" to intentKey,
                    "viewModel" to this::class.simpleName.orEmpty(),
                    "errorType" to "intent_processing"
                ))
                handleIntentError(intent, e)
            }
        }
    }

    /**
     * Override this to handle different intents
     * This is where your business logic goes
     */
    protected abstract suspend fun processIntent(intent: Intent)

    /**
     * Override to specify which intents should be deduplicated
     * Useful for expensive operations like network calls
     */
    protected open fun shouldDeduplicateIntent(intent: Intent): Boolean {
        return intent::class.simpleName?.contains("Load", ignoreCase = true) == true ||
               intent::class.simpleName?.contains("Refresh", ignoreCase = true) == true
    }


    /**
     * Handle errors specific to intent processing
     * Integrates with BaseViewModel's error handling infrastructure
     */
    protected open suspend fun handleIntentError(intent: Intent, error: Throwable) {
        // BaseViewModel already handles logging and analytics
        handleErrorSideEffect(error)
    }

    /**
     * Override this to emit error side effects specific to your screen
     * This avoids unchecked cast warnings by letting each ViewModel handle its own side effects
     */
    protected open suspend fun handleErrorSideEffect(error: Throwable) {
        // Override in subclasses to emit appropriate error side effects
        // emitSideEffect(CoinsSideEffect.ShowError(error.message ?: "Unknown error"))
    }

    /**
     * Thread-safe state updates with validation
     */
    protected suspend fun updateState(reducer: (State) -> State) {
        stateMutex.withLock {
            val oldState = currentState
            val newState = reducer(oldState)

            // Skip update if state hasn't actually changed
            if (oldState == newState) return@withLock

            validateState(newState)


            // Analytics: Track state changes
            logger.debug(TAG, "State changed from ${oldState::class.simpleName} to ${newState::class.simpleName}")

            _state.value = newState
        }
    }


    /**
     * Validate state before updating (override for custom validation)
     */
    protected open fun validateState(state: State) {
        // Override in subclasses for custom validation
    }

    /**
     * Emit side effect with comprehensive error handling
     */
    protected suspend fun emitSideEffect(effect: SideEffect) {
        try {
            _sideEffect.send(effect)
            logger.debug(TAG, "Side effect emitted: ${effect::class.simpleName}")
        } catch (e: Exception) {
            logger.error(TAG, "Failed to emit side effect: ${effect::class.simpleName}", e)
            analytics.logError(e, mapOf(
                "sideEffectType" to effect::class.simpleName.orEmpty(),
                "viewModel" to this::class.simpleName.orEmpty(),
                "errorType" to "side_effect_emission"
            ))
        }
    }








    override fun onCleared() {
        super.onCleared()
        _sideEffect.cancel()
        runningIntents.clear()
    }
}