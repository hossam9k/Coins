# Clean Architecture Recommendations

## Current Structure Analysis

### ✅ What's Good:
- Feature-based modularization (coins, portfolio, trade)
- Clean separation of data/domain/presentation layers per feature
- Well-organized design system
- Proper use of MVI pattern

### ⚠️ Issues to Address:
1. **Confusing naming**: `shared/business` mixes different concerns
2. **Over-nesting**: Too many subdirectories for simple functionality
3. **Duplicate code**: Multiple mapper folders doing similar things
4. **Mixed responsibilities**: Database, network, utils all under "business"

## Recommended Structure

```
📁 composeApp/src/commonMain/kotlin/org/poc/app/
│
├── 📁 core/                           # Core functionality (was: shared)
│   ├── 📁 data/
│   │   ├── 📁 network/
│   │   │   ├── HttpClient.kt         # Ktor client setup
│   │   │   └── NetworkConstants.kt   # API URLs, timeouts
│   │   └── 📁 database/
│   │       └── PortfolioDatabase.kt  # Database setup
│   │
│   ├── 📁 domain/
│   │   ├── 📁 model/                 # Shared domain models
│   │   └── 📁 repository/            # Base repository interfaces
│   │
│   ├── 📁 presentation/
│   │   ├── 📁 base/                  # Base MVI classes
│   │   │   ├── BaseViewModel.kt
│   │   │   └── MviIntent.kt
│   │   └── 📁 util/                  # UI utilities
│   │       └── Formatter.kt
│   │
│   └── 📁 di/                        # Dependency injection
│       └── Modules.kt
│
├── 📁 features/                       # Feature modules (was: feature)
│   ├── 📁 coins/
│   │   ├── 📁 data/
│   │   │   ├── CoinRepository.kt     # Repository implementation
│   │   │   ├── CoinApi.kt           # API interface
│   │   │   └── CoinDto.kt           # Data transfer objects
│   │   ├── 📁 domain/
│   │   │   ├── Coin.kt              # Domain model
│   │   │   └── GetCoinsUseCase.kt   # Business logic
│   │   └── 📁 ui/                   # Presentation layer
│   │       ├── CoinsScreen.kt       # UI
│   │       ├── CoinsViewModel.kt    # State management
│   │       └── CoinsUiModel.kt      # UI models
│   │
│   ├── 📁 portfolio/                 # Same structure
│   └── 📁 trade/                     # Same structure
│
├── 📁 ui/                            # App-level UI (was: top level)
│   ├── 📁 design/                    # Design system (keep as-is!)
│   ├── 📁 navigation/                # Navigation setup
│   └── 📁 theme/                     # Simplified theme
│
└── App.kt                            # Application entry point
```

## Migration Steps (Priority Order)

### 1. Quick Wins (No Code Changes)
```bash
# Just rename folders for clarity
shared/business → core
feature → features
presentation → ui (in each feature)
```

### 2. Flatten Structure
- Remove unnecessary nesting like `data/remote/api/` → just `data/`
- Combine similar mappers into single files
- Move utilities to appropriate layers

### 3. Simplify Shared/Core
- Extract database to `core/data/database`
- Extract network to `core/data/network`
- Remove "business" naming

## Benefits

1. **Clearer boundaries** - Each layer has distinct responsibility
2. **Less nesting** - Easier to navigate
3. **Consistent naming** - Same structure in every feature
4. **Reduced duplication** - Shared code in core
5. **Better scalability** - Easy to add new features

## Example: Simplified Coins Feature

### Before (7 levels deep):
```
feature/coins/data/remote/api/impl/CoinsApiImpl.kt
feature/coins/presentation/mapper/CoinUiMapper.kt
```

### After (4 levels deep):
```
features/coins/data/CoinRepository.kt
features/coins/ui/CoinsScreen.kt
```

## Don'ts

❌ Don't over-engineer with too many layers
❌ Don't create abstractions you don't need
❌ Don't separate by technical concern (all repositories together)
❌ Don't use generic names like "business" or "utils"

## Do's

✅ Keep it simple - 3 layers per feature is enough
✅ Feature-first organization
✅ Clear, descriptive names
✅ Colocate related code