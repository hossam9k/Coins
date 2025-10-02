package org.poc.app.core.presentation.base

import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger

/**
 * MVI TEMPLATE FOR NEW SCREENS
 * Copy this template for each new screen in your app
 *
 * Replace "ScreenName" with your actual screen name:
 * - UserListViewModel
 * - DashboardViewModel
 * - SettingsViewModel
 * - ReportsViewModel
 * etc.
 */

// ================================
// 1. SCREEN-SPECIFIC STATE
// ================================

/**
 * Replace "ScreenName" with your screen name: UserListState, DashboardState, etc.
 */
data class ScreenNameState(
    val data: List<ScreenDataModel> = emptyList(),
    val selectedItem: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
) : UiState

// ================================
// 2. SCREEN-SPECIFIC INTENTS
// ================================

/**
 * Replace "ScreenName" with your screen name: UserListIntent, DashboardIntent, etc.
 * Add intents specific to what users can do on THIS screen
 */
sealed interface ScreenNameIntent : UiIntent {
    // Loading intents
    data object LoadScreenData : ScreenNameIntent

    data object RefreshScreenData : ScreenNameIntent

    // User interaction intents
    data class SelectItem(
        val itemId: String,
    ) : ScreenNameIntent

    data class PerformAction(
        val actionType: String,
        val itemId: String,
    ) : ScreenNameIntent

    // Navigation intents
    data object NavigateToDetails : ScreenNameIntent

    data object GoBack : ScreenNameIntent

    // Error handling
    data object RetryAfterError : ScreenNameIntent

    data object DismissError : ScreenNameIntent
}

// ================================
// 3. SCREEN-SPECIFIC SIDE EFFECTS
// ================================

/**
 * Replace "ScreenName" with your screen name: UserListSideEffect, DashboardSideEffect, etc.
 * Add side effects specific to THIS screen's needs
 */
sealed interface ScreenNameSideEffect : UiSideEffect {
    // Error handling
    data class ShowError(
        val message: String,
    ) : ScreenNameSideEffect

    data class ShowSuccess(
        val message: String,
    ) : ScreenNameSideEffect

    // Navigation
    data class NavigateToScreen(
        val route: String,
    ) : ScreenNameSideEffect

    data object NavigateBack : ScreenNameSideEffect

    // UI actions
    data object ShowLoadingDialog : ScreenNameSideEffect

    data object HideLoadingDialog : ScreenNameSideEffect

    data class ShowConfirmDialog(
        val message: String,
        val action: ScreenNameIntent,
    ) : ScreenNameSideEffect
}

// ================================
// 4. SCREEN-SPECIFIC VIEWMODEL
// ================================

/**
 * Replace "ScreenName" with your screen name: UserListViewModel, DashboardViewModel, etc.
 */
class ScreenNameViewModel(
    private val screenRepository: ScreenNameRepository, // Your use cases/repository
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
    // Add other dependencies your screen needs
) : MviViewModel<ScreenNameState, ScreenNameIntent, ScreenNameSideEffect>(
        initialState = ScreenNameState(),
        dispatcherProvider = dispatcherProvider,
        logger = logger,
        analytics = analytics,
        sideEffectBufferSize = 64,
    ) {
    init {
        // Auto-load data when screen opens
        handleIntent(ScreenNameIntent.LoadScreenData)
    }

    // ================================
    // INTENT PROCESSING
    // ================================

    override suspend fun processIntent(intent: ScreenNameIntent) {
        when (intent) {
            // Loading
            is ScreenNameIntent.LoadScreenData -> loadData()
            is ScreenNameIntent.RefreshScreenData -> refreshData()

            // User interactions
            is ScreenNameIntent.SelectItem -> selectItem(intent.itemId)
            is ScreenNameIntent.PerformAction -> performAction(intent.actionType, intent.itemId)

            // Navigation
            is ScreenNameIntent.NavigateToDetails -> navigateToDetails()
            is ScreenNameIntent.GoBack -> goBack()

            // Error handling
            is ScreenNameIntent.RetryAfterError -> retryAfterError()
            is ScreenNameIntent.DismissError -> dismissError()
        }
    }

    // ================================
    // BUSINESS LOGIC METHODS
    // ================================

    private suspend fun loadData() {
        updateState { it.copy(isLoading = true, error = null) }

        try {
            val data = screenRepository.getData()
            updateState {
                it.copy(
                    data = data,
                    isLoading = false,
                    error = null,
                )
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoading = false,
                    error = "Failed to load data",
                )
            }
            emitSideEffect(ScreenNameSideEffect.ShowError("Failed to load data"))
        }
    }

    private suspend fun refreshData() {
        updateState { it.copy(isRefreshing = true) }

        try {
            val data = screenRepository.refreshData()
            updateState {
                it.copy(
                    data = data,
                    isRefreshing = false,
                    error = null,
                )
            }
            emitSideEffect(ScreenNameSideEffect.ShowSuccess("Data refreshed"))
        } catch (e: Exception) {
            updateState { it.copy(isRefreshing = false) }
            emitSideEffect(ScreenNameSideEffect.ShowError("Failed to refresh"))
        }
    }

    private suspend fun selectItem(itemId: String) {
        updateState { it.copy(selectedItem = itemId) }
        emitSideEffect(ScreenNameSideEffect.ShowSuccess("Item selected"))
    }

    private suspend fun performAction(
        actionType: String,
        itemId: String,
    ) {
        when (actionType) {
            "delete" -> {
                emitSideEffect(
                    ScreenNameSideEffect.ShowConfirmDialog(
                        message = "Delete this item?",
                        action = ScreenNameIntent.PerformAction("confirm_delete", itemId),
                    ),
                )
            }
            "confirm_delete" -> {
                try {
                    screenRepository.deleteItem(itemId)
                    updateState {
                        it.copy(data = it.data.filter { item -> item.id != itemId })
                    }
                    emitSideEffect(ScreenNameSideEffect.ShowSuccess("Item deleted"))
                } catch (e: Exception) {
                    emitSideEffect(ScreenNameSideEffect.ShowError("Failed to delete"))
                }
            }
        }
    }

    private suspend fun navigateToDetails() {
        val selectedId = currentState.selectedItem
        if (selectedId != null) {
            emitSideEffect(ScreenNameSideEffect.NavigateToScreen("details/$selectedId"))
        }
    }

    private suspend fun goBack() {
        emitSideEffect(ScreenNameSideEffect.NavigateBack)
    }

    private suspend fun retryAfterError() {
        loadData()
    }

    private suspend fun dismissError() {
        updateState { it.copy(error = null) }
    }

    // ================================
    // OPTIONAL OVERRIDES
    // ================================

    override fun shouldDeduplicateIntent(intent: ScreenNameIntent): Boolean =
        when (intent) {
            is ScreenNameIntent.LoadScreenData,
            is ScreenNameIntent.RefreshScreenData,
            -> true
            else -> false
        }
}

// ================================
// REPOSITORY INTERFACE
// ================================

interface ScreenNameRepository {
    suspend fun getData(): List<ScreenDataModel>

    suspend fun refreshData(): List<ScreenDataModel>

    suspend fun deleteItem(id: String)
}

// ================================
// DATA MODELS
// ================================

data class ScreenDataModel(
    val id: String,
    val title: String,
    val description: String,
)

// ================================
// USAGE PATTERNS FOR DIFFERENT SCREENS:
// ================================

/**
 * USER LIST SCREEN:
 * - UserListState, UserListIntent, UserListSideEffect
 * - UserListViewModel
 * - UserListRepository
 *
 * DASHBOARD SCREEN:
 * - DashboardState, DashboardIntent, DashboardSideEffect
 * - DashboardViewModel
 * - DashboardRepository
 *
 * SETTINGS SCREEN:
 * - SettingsState, SettingsIntent, SettingsSideEffect
 * - SettingsViewModel
 * - SettingsRepository
 *
 * REPORTS SCREEN:
 * - ReportsState, ReportsIntent, ReportsSideEffect
 * - ReportsViewModel
 * - ReportsRepository
 */
