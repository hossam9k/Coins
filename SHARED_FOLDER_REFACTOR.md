# Shared Folder Refactoring Guide

## Current Issues

1. **Unclear Separation**: `core/` and `shared/` both contain shared code
2. **Mixed Layers**: UI (design) mixed with DI in `shared/`
3. **Naming Confusion**: "shared" is too generic

## Recommended Refactoring

### Option 1: Merge Everything into Core (Simplest) ✅

```bash
# Move DI to core
mv shared/di → core/di

# Move design to ui folder
mv shared/design → ui/

# Remove shared folder
rm -rf shared/
```

**Result:**
```
app/
├── core/        # All shared business/infra
├── features/    # Feature modules
├── ui/          # All UI/design related
└── navigation/  # Navigation logic
```

### Option 2: Clear Separation by Layer (Clean Architecture) 🏆

```bash
# Infrastructure layer
core/
├── data/        # Network, DB
├── di/          # Dependency injection
└── util/        # Common utilities

# Presentation layer
ui/
├── design/      # Design system
├── theme/       # Theming
└── components/  # Shared UI components

# Feature layer
features/
└── [feature]/   # Feature modules

# App layer
app/
├── navigation/  # Navigation
└── App.kt       # Entry point
```

## Naming Convention Improvements

### Current (Confusing):
- `shared/` - Too generic
- `business/` - Unclear
- `design/` - Good but in wrong place

### Recommended:
- `core/` - Infrastructure & business logic
- `ui/` - All presentation layer
- `features/` - Feature modules
- `app/` - App-level configuration

## Implementation Steps

1. **Create ui folder**
```bash
mkdir -p composeApp/src/commonMain/kotlin/org/poc/app/ui
```

2. **Move design system**
```bash
mv composeApp/src/commonMain/kotlin/org/poc/app/shared/design \
   composeApp/src/commonMain/kotlin/org/poc/app/ui/design
```

3. **Move DI to core**
```bash
mv composeApp/src/commonMain/kotlin/org/poc/app/shared/di \
   composeApp/src/commonMain/kotlin/org/poc/app/core/di
```

4. **Update imports**
```bash
# Update all imports from shared.design to ui.design
find . -name "*.kt" -exec sed -i '' 's/shared\.design/ui\.design/g' {} \;

# Update all imports from shared.di to core.di
find . -name "*.kt" -exec sed -i '' 's/shared\.di/core\.di/g' {} \;
```

5. **Remove shared folder**
```bash
rm -rf composeApp/src/commonMain/kotlin/org/poc/app/shared
```

## Benefits After Refactoring

✅ **Clear Layer Separation**
- Core = Business/Infrastructure
- UI = Presentation
- Features = Feature modules

✅ **Better Naming**
- No ambiguous "shared" folder
- Clear responsibility per folder

✅ **Follows Clean Architecture**
- Dependency rule maintained
- Clear boundaries

✅ **Easier to Navigate**
- Developers know where to find things
- Consistent with industry standards