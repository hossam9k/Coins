# Architecture Standards

## Package Naming Conventions

This project follows a **feature-based Clean Architecture** pattern with the following package structure:

### 1. Root Package Structure
```
org.poc.app/
├── core/                   # Shared infrastructure and utilities
├── design/                 # Design system and UI components
├── {feature}/              # Feature-specific modules
├── di/                     # Dependency injection configuration
└── navigation/             # Navigation definitions (planned)
```

### 2. Feature Package Structure
Each feature follows Clean Architecture layers:

```
{feature}/
├── data/                   # Data layer
│   ├── local/             # Local data sources (Database, Cache)
│   ├── remote/            # Remote data sources (API)
│   ├── repository/        # Repository implementations
│   └── mapper/            # Data mapping utilities
├── domain/                # Business logic layer
│   ├── model/             # Domain models
│   ├── repository/        # Repository interfaces
│   └── usecase/           # Business use cases
└── presentation/          # Presentation layer
    ├── {screen}/          # Screen-specific components
    ├── model/             # UI models and states
    └── mapper/            # Presentation mapping utilities
```

### 3. Core Package Structure
```
core/
├── data/                  # Core data utilities
├── database/              # Database configuration
├── domain/                # Core business entities
│   ├── model/            # Shared domain models
│   ├── error/            # Error definitions
│   └── util/             # Domain utilities
├── network/               # Network configuration
├── presentation/          # Core presentation utilities
│   ├── mvi/              # MVI framework
│   └── component/        # Shared UI components
└── util/                  # General utilities
```

### 4. Design System Structure
```
design/
├── components/            # Reusable UI components
│   ├── button/           # Button components
│   ├── input/            # Input components
│   ├── list/             # List components
│   └── dialog/           # Dialog components
├── foundation/            # Design tokens
│   ├── color/            # Color definitions
│   ├── typography/       # Typography scale
│   └── spacing/          # Spacing tokens
└── theme/                 # Theme implementations
```

## Naming Conventions

### 1. Package Names
- Use lowercase letters
- Use single words when possible
- Use underscores for compound words: `user_profile`
- Be descriptive but concise

### 2. Class Names
- **Models**: `{Entity}Model` (e.g., `CoinModel`)
- **DTOs**: `{Entity}Dto` (e.g., `CoinDto`)
- **Entities**: `{Entity}Entity` (e.g., `PortfolioCoinEntity`)
- **Use Cases**: `{Action}{Entity}UseCase` (e.g., `GetCoinDetailsUseCase`)
- **Repositories**: `{Entity}Repository` (e.g., `CoinsRepository`)
- **Repository Implementations**: `{Entity}RepositoryImpl`
- **ViewModels**: `{Screen}ViewModel` (e.g., `CoinsListViewModel`)
- **UI States**: `{Screen}State` (e.g., `PortfolioState`)
- **UI Intents**: `{Screen}Intent` (e.g., `CoinsIntent`)
- **Side Effects**: `{Screen}SideEffect` (e.g., `SellSideEffect`)

### 3. Function Names
- Use camelCase
- Start with a verb: `getCoinDetails`, `updatePortfolio`
- Boolean functions start with `is`, `has`, `can`: `isLoading`, `hasError`

### 4. File Names
- Match the main class name
- Use PascalCase for classes: `CoinModel.kt`
- Use camelCase for functions: `formatCurrency.kt`

## Module Organization

### Current Structure
- **Single Module**: All code in `composeApp` module

### Recommended Evolution
For larger projects, consider splitting into modules:

```
:core                      # Core utilities and infrastructure
:design                    # Design system
:feature:coins            # Coins feature
:feature:portfolio        # Portfolio feature
:feature:trade            # Trading feature
:navigation               # Navigation definitions
```

## Dependencies Flow

```
Presentation Layer
       ↓
Domain Layer (Business Logic)
       ↓
Data Layer (Repository Pattern)
       ↓
External Sources (API, Database)
```

### Dependency Rules
1. **Domain layer** has no dependencies on other layers
2. **Presentation layer** depends only on domain layer
3. **Data layer** implements domain interfaces
4. **Core** can be used by all layers
5. **Design** can be used by presentation layer

## Error Handling

### Error Types Hierarchy
```
Error (interface)
├── DataError
│   ├── Remote (network errors)
│   └── Local (database errors)
├── DomainError
│   ├── ValidationError
│   └── BusinessLogicError
└── PresentationError
    ├── UIError
    └── NavigationError
```

## Testing Structure

```
src/
├── commonTest/           # Shared tests
│   ├── {feature}/       # Feature-specific tests
│   └── core/            # Core utilities tests
├── androidUnitTest/     # Android-specific unit tests
└── iosTest/            # iOS-specific tests
```

## Best Practices

1. **Single Responsibility**: Each class should have one reason to change
2. **Dependency Inversion**: Depend on abstractions, not concretions
3. **Interface Segregation**: Create specific interfaces
4. **Clean Boundaries**: Clear separation between layers
5. **Testability**: All components should be easily testable
6. **Consistency**: Follow naming conventions consistently
7. **Documentation**: Document complex business logic
8. **Error Handling**: Use typed errors with Result pattern