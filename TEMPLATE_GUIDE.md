# 🚀 KMP Template Codebase Guide

## Quick Start

Use this project as a template for new KMP applications. It includes:
- ✅ Clean Architecture
- ✅ Material Design 3
- ✅ Type-safe Navigation
- ✅ Dependency Injection (Koin)
- ✅ Network Layer (Ktor)
- ✅ Database (Room)
- ✅ MVI Pattern

## Project Structure

```
app/
├── core/                      # Shared/Core functionality
│   ├── config/               # App configuration
│   ├── data/                 # Network, Database
│   ├── domain/               # Business logic
│   └── presentation/         # Base UI components
│
├── features/                  # Feature modules
│   └── [feature_name]/
│       ├── data/             # Repository impl
│       ├── domain/           # Use cases
│       └── presentation/     # UI (Screen, ViewModel)
│
├── navigation/               # Navigation setup
│   ├── NavigationHost.kt    # Nav graph
│   └── NavigationRoute.kt   # Routes
│
└── shared/design/            # Design system
    ├── components/           # Reusable UI
    └── theme/               # Colors, Typography
```

## Adding a New Feature

### 1. Create Feature Structure
```kotlin
features/
└── mynewfeature/
    ├── data/
    │   └── MyFeatureRepository.kt
    ├── domain/
    │   └── MyFeatureUseCase.kt
    └── presentation/
        ├── MyFeatureScreen.kt
        └── MyFeatureViewModel.kt
```

### 2. Add Navigation Route
```kotlin
// In NavigationRoute.kt
@Serializable
data object MyNewFeature : NavigationRoute
```

### 3. Add to Navigation Graph
```kotlin
// In NavigationHost.kt
composable<NavigationRoute.MyNewFeature> {
    MyFeatureScreen()
}
```

### 4. Register in DI
```kotlin
// In Module.kt
single { MyFeatureRepository() }
single { MyFeatureUseCase(get()) }
viewModel { MyFeatureViewModel(get()) }
```

## Customization Checklist

When using as template, update:

- [ ] Package name (org.poc.app → your.package)
- [ ] App name in `AppConfiguration`
- [ ] API URLs in `BuildConfig`
- [ ] Theme colors in `Color.kt`
- [ ] App icon and splash screen
- [ ] Remove sample features (coins, portfolio, trade)

## Best Practices

### DO's ✅
- Keep features independent
- Use Material Design components
- Handle loading/error states consistently
- Follow MVI pattern for state management
- Test on both Android and iOS

### DON'T's ❌
- Don't create deep folder nesting (max 4 levels)
- Don't hardcode strings (use resources)
- Don't skip error handling
- Don't mix business logic with UI
- Don't overcomplicate - KISS principle

## Common Tasks

### Change API Endpoint
```kotlin
// core/config/AppConfig.kt
object BuildConfig {
    const val API_BASE_URL = "https://your-api.com"
}
```

### Add Custom Color
```kotlin
// Just use Material colors!
// Only add business colors if absolutely needed
```

### Add New Screen
1. Create Screen composable
2. Create ViewModel
3. Add Route
4. Add to NavGraph

### Handle Deep Links
```kotlin
composable<NavigationRoute.MyScreen>(
    deepLinks = listOf(
        navDeepLink { uriPattern = "app://myscreen/{id}" }
    )
)
```

## Architecture Decisions

- **Navigation**: Type-safe with Navigation Compose 2.8+
- **DI**: Koin (simpler than Dagger for KMP)
- **Network**: Ktor (KMP native)
- **Database**: Room (now supports KMP)
- **State**: MVI pattern with StateFlow
- **Theme**: Material 3 with dynamic colors

## Maintenance

### Update Dependencies
```bash
./gradlew dependencyUpdates
```

### Clean Build
```bash
./gradlew clean build
```

### Run Tests
```bash
./gradlew test
./gradlew iosSimulatorArm64Test
```

## Support

This template follows:
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)

---

**Ready to build your next KMP app! 🚀**