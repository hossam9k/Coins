package org.poc.app.shared.business.presentation.mvi

/**
 * Base MVI contracts for type-safe, reusable architecture
 *
 * These interfaces ensure consistency across all features while maintaining flexibility
 * Usage: Extend these in your feature-specific contracts
 */

/**
 * Marker interface for all UI states
 * Ensures type safety and consistency across features
 */
interface UiState

/**
 * Marker interface for all user intents/actions
 * Provides type safety for intent handling
 */
interface UiIntent

/**
 * Marker interface for all one-time side effects
 * Handles navigation, snackbars, dialogs, etc.
 */
interface UiSideEffect

/**
 * Common loading states that most features need
 * Extend this for feature-specific states
 */
interface LoadingUiState : UiState {
    val isLoading: Boolean
    val isRefreshing: Boolean
}

/**
 * Common error states that most features need
 * Extend this for feature-specific error handling
 */
interface ErrorUiState : UiState {
    val error: String?
    val errorDetails: String?
}

/**
 * Combination of common states most features need
 * Provides loading and error handling out of the box
 */
interface CommonUiState : LoadingUiState, ErrorUiState

/**
 * Common intents that most features need
 * Extend this interface for feature-specific intents
 */
interface CommonUiIntent : UiIntent {
    // Most features need these basic intents
    interface Load : CommonUiIntent
    interface Refresh : CommonUiIntent
    interface Retry : CommonUiIntent
    interface Clear : CommonUiIntent
}

/**
 * Common side effects that most features need
 * Extend this for feature-specific side effects
 */
interface CommonUiSideEffect : UiSideEffect {
    data class ShowError(
        val message: String,
        val details: String? = null,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : CommonUiSideEffect

    data class ShowSuccess(
        val message: String
    ) : CommonUiSideEffect

    data class Navigate(
        val route: String,
        val clearBackStack: Boolean = false
    ) : CommonUiSideEffect

    data class NavigateUp(
        val result: Any? = null
    ) : CommonUiSideEffect
}

/**
 * Result wrapper for handling loading/success/error states
 * Provides consistent state management across features
 */
sealed class UiResult<out T> {
    data object Idle : UiResult<Nothing>()
    data object Loading : UiResult<Nothing>()
    data class Success<T>(val data: T) : UiResult<T>()
    data class Error(
        val message: String,
        val details: String? = null,
        val throwable: Throwable? = null
    ) : UiResult<Nothing>()
}

/**
 * Extension functions for UiResult to make state handling easier
 */
val <T> UiResult<T>.isLoading: Boolean get() = this is UiResult.Loading
val <T> UiResult<T>.isSuccess: Boolean get() = this is UiResult.Success
val <T> UiResult<T>.isError: Boolean get() = this is UiResult.Error
val <T> UiResult<T>.isIdle: Boolean get() = this is UiResult.Idle

fun <T> UiResult<T>.getDataOrNull(): T? = (this as? UiResult.Success)?.data
fun <T> UiResult<T>.getErrorOrNull(): UiResult.Error? = this as? UiResult.Error