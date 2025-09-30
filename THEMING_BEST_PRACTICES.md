# Compose Multiplatform Theming Best Practices

## Architecture Overview

### ✅ **Recommended Approach:**
1. **MaterialTheme.colorScheme** - For standard UI colors (background, surface, primary, etc.)
2. **CompositionLocal** - For custom brand/domain colors (profit/loss, brand colors)
3. **DesignSystem object** - For typography, spacing, shapes (non-color tokens)

### ❌ **Avoid:**
- Creating abstractions over MaterialTheme.colorScheme
- Complex wrapper layers for standard colors
- Mixing approaches inconsistently

## Implementation Structure

```kotlin
// 1. Custom domain colors
@Immutable
data class FinancialColors(
    val profit: Color = Color.Unspecified,
    val loss: Color = Color.Unspecified,
    val neutral: Color = Color.Unspecified,
)

val LocalFinancialColors = compositionLocalOf { FinancialColors() }

// 2. Use MaterialTheme for standard colors
Text(
    text = "Background text",
    color = MaterialTheme.colorScheme.onBackground // ✅ Direct usage
)

// 3. Use CompositionLocal for custom colors
val financialColors = LocalFinancialColors.current
Text(
    text = "+5.2%",
    color = financialColors.profit // ✅ Custom domain color
)
```

## Why This Approach Works

### **MaterialTheme.colorScheme Advantages:**
- ✅ Battle-tested by Google
- ✅ Automatic dark/light mode handling
- ✅ Accessibility built-in
- ✅ Platform-specific optimizations
- ✅ No abstraction bugs
- ✅ IDE autocomplete and tooling support

### **CompositionLocal for Custom Colors:**
- ✅ Type-safe custom colors
- ✅ Theme-aware switching
- ✅ Performance optimized (@Immutable)
- ✅ Follows Compose patterns
- ✅ Easy to test and maintain

## File Structure

```
design/
├── theme/
│   ├── colors/
│   │   ├── MaterialColors.kt          // Material color definitions
│   │   └── CustomColors.kt            // Brand/domain colors
│   ├── typography/
│   │   └── Typography.kt
│   ├── spacing/
│   │   └── Spacing.kt
│   └── AppTheme.kt                    // Main theme composable
└── tokens/
    └── DesignTokens.kt                // Non-color design tokens
```

## Real-World Examples

### **Google's Official Apps:**
- Use MaterialTheme.colorScheme directly
- CompositionLocal for brand colors only
- No abstraction layers over standard colors

### **Popular Open Source Projects:**
- JetBrains Compose samples
- Now in Android (Google)
- Compose Samples repository

All follow this hybrid approach!

## Performance Benefits

```kotlin
// ❌ Slow - Extra abstraction layer
color = DesignSystem.DynamicColors.OnBackground

// ✅ Fast - Direct access
color = MaterialTheme.colorScheme.onBackground

// ✅ Fast - Optimized CompositionLocal
color = LocalFinancialColors.current.profit
```