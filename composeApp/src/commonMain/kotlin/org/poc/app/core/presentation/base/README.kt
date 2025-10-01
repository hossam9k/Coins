package org.poc.app.core.presentation.base

/**
 * MVI ARCHITECTURE FILES GUIDE
 * ============================
 *
 * CORE ARCHITECTURE:
 * ✅ MviContract.kt      - Base interfaces (UiState, UiIntent, UiSideEffect)
 * ✅ MviViewModel.kt     - Production-ready base ViewModel with all features
 *
 * TEMPLATES & REFERENCES:
 * ✅ MviTemplate.kt - Copy this template for new screens
 * ✅ Real example - See /portfolio/presentation/PortfolioViewModel.kt for production implementation
 *
 * HOW TO CREATE A NEW SCREEN:
 * ===========================
 *
 * 1. Copy MviTemplate.kt
 * 2. Replace "ScreenName" with your actual screen:
 *    - UserListViewModel (for user list screen)
 *    - DashboardViewModel (for dashboard screen)
 *    - SettingsViewModel (for settings screen)
 *    - ReportsViewModel (for reports screen)
 *
 * 3. Replace contracts:
 *    - ScreenNameState → UserListState, DashboardState, etc.
 *    - ScreenNameIntent → UserListIntent, DashboardIntent, etc.
 *    - ScreenNameSideEffect → UserListSideEffect, DashboardSideEffect, etc.
 *
 * 4. Add screen-specific business logic
 * 5. Add to DI module: viewModel { UserListViewModel(get(), get()) }
 *
 * NAMING RULES:
 * =============
 *
 * ✅ SCREEN-SPECIFIC NAMING:
 * - UserListIntent.LoadUsers, DashboardIntent.RefreshData
 * - UserListSideEffect.ShowError, DashboardSideEffect.NavigateToDetails
 * - UserListState, DashboardState, SettingsState
 *
 * RULE: Use screen name + action/state type for clarity
 *
 * ARCHITECTURE BENEFITS:
 * ======================
 *
 * 🚀 Production Features:
 * - Thread-safe state management
 * - Intent deduplication
 * - Performance analytics
 * - State history (time travel debugging)
 * - Comprehensive error handling
 * - Memory leak prevention
 *
 * 🎯 Clean Code:
 * - Screen-specific naming
 * - Type-safe contracts
 * - Predictable state updates
 * - Testable architecture
 * - Easy to maintain
 * - Team-friendly code
 */

// This file is just documentation - no actual code here
// Use the other files for implementation