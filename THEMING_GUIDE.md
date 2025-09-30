# POC App Theming Guide

## Overview
This guide explains how to use the enhanced theming system in our KMP POC application to ensure consistent appearance across light and dark modes on all platforms.

## Color System Architecture

### ✅ DynamicColors (Recommended)
Use `DesignSystem.DynamicColors` for all @Composable functions:

```kotlin
Text(
    text = "Hello World",
    color = DesignSystem.DynamicColors.OnBackground
)

Box(
    modifier = Modifier.background(DesignSystem.DynamicColors.Surface)
)
```

**Benefits:**
- Automatically adapts to light/dark mode
- Consistent across Android and iOS
- Proper contrast ratios maintained
- MaterialTheme integration

### ⚠️ Static Colors (Deprecated)
`DesignSystem.Colors` is deprecated and should be avoided:

```kotlin
// ❌ DON'T DO THIS - Fixed colors that don't adapt to theme
Text(
    text = "Hello World",
    color = DesignSystem.Colors.OnBackground // Always black, invisible in dark mode!
)
```

## Available Colors

### Primary Colors
- `DesignSystem.DynamicColors.Primary` - Main brand color
- `DesignSystem.DynamicColors.OnPrimary` - Text/icons on primary color
- `DesignSystem.DynamicColors.PrimaryContainer` - Tinted container color
- `DesignSystem.DynamicColors.OnPrimaryContainer` - Text/icons on primary container

### Surface Colors
- `DesignSystem.DynamicColors.Surface` - Component surfaces (cards, sheets)
- `DesignSystem.DynamicColors.OnSurface` - Text/icons on surface
- `DesignSystem.DynamicColors.SurfaceVariant` - Variant surface color
- `DesignSystem.DynamicColors.OnSurfaceVariant` - Text/icons on surface variant

### Background Colors
- `DesignSystem.DynamicColors.Background` - Screen background
- `DesignSystem.DynamicColors.OnBackground` - Text/icons on background

### Error Colors
- `DesignSystem.DynamicColors.Error` - Error states and messages
- `DesignSystem.DynamicColors.OnError` - Text/icons on error color

### Utility Colors
- `DesignSystem.DynamicColors.Outline` - Borders and dividers
- `DesignSystem.DynamicColors.OutlineVariant` - Subtle borders

## Component Styling Best Practices

### Using Color.orDefault()
When creating reusable components with optional styling:

```kotlin
data class MyComponentStyling(
    val textColor: Color = Color.Unspecified,
    val backgroundColor: Color = Color.Unspecified
)

@Composable
fun MyComponent(styling: MyComponentStyling = MyComponentStyling()) {
    Text(
        text = "Sample",
        color = styling.textColor.orDefault { DesignSystem.DynamicColors.OnBackground }
    )
}
```

### Data Class Defaults
Always use `Color.Unspecified` as defaults in styling data classes:

```kotlin
// ✅ CORRECT - Theme-safe defaults
data class ComponentStyling(
    val textColor: Color = Color.Unspecified,  // Will use dynamic default
    val backgroundColor: Color = Color.Unspecified
)

// ❌ INCORRECT - Static color that won't adapt
data class ComponentStyling(
    val textColor: Color = DesignSystem.Colors.Primary  // Compilation warning!
)
```

## Common Patterns

### Screen Backgrounds
```kotlin
@Composable
fun MyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DesignSystem.DynamicColors.Background)
    ) {
        // Screen content
    }
}
```

### Cards and Surfaces
```kotlin
@Composable
fun MyCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DesignSystem.DynamicColors.Surface
        )
    ) {
        Text(
            text = "Card content",
            color = DesignSystem.DynamicColors.OnSurface
        )
    }
}
```

### Buttons with Financial Colors
```kotlin
@Composable
fun BuyButton() {
    val financialColors = LocalFinancialColors.current

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = financialColors.profit
        )
    ) {
        Text(
            text = "Buy",
            color = DesignSystem.DynamicColors.OnPrimary
        )
    }
}
```

## Migration Guide

### Step 1: Find and Replace Static Colors
Use your IDE to find all usages of `DesignSystem.Colors.` and replace with `DesignSystem.DynamicColors.`

### Step 2: Update Component Data Classes
Replace static color defaults with `Color.Unspecified`

### Step 3: Use orDefault() Extension
For components that need flexible styling, use the `orDefault()` extension function

### Step 4: Test Both Themes
Always test your screens in both light and dark modes on both Android and iOS

## Build-Time Checks

The project includes detekt rules to catch static color usage. Run:
```bash
./gradlew detekt
```

## Troubleshooting

### Text Not Visible in Dark Mode
**Problem:** Text appears black on dark background
**Solution:** Use `DesignSystem.DynamicColors.OnBackground` instead of `DesignSystem.Colors.OnBackground`

### Component Doesn't Adapt to Theme
**Problem:** Component colors don't change when switching themes
**Solution:** Ensure all colors use `DynamicColors` and are accessed within @Composable functions

### Build Errors with @Composable Colors
**Problem:** Cannot use dynamic colors in data class defaults
**Solution:** Use `Color.Unspecified` as default and apply dynamic colors with `orDefault()`

## Testing Checklist

Before merging theme-related changes:
- [ ] Test light mode on Android
- [ ] Test dark mode on Android
- [ ] Test light mode on iOS
- [ ] Test dark mode on iOS
- [ ] Verify proper contrast ratios
- [ ] Check that no text is invisible
- [ ] Ensure buttons have proper styling
- [ ] Validate component reusability

## Financial Colors

The app includes special financial colors for profit/loss visualization:
- `LocalFinancialColors.current.profit` - Green for positive values
- `LocalFinancialColors.current.loss` - Red for negative values

These adapt to the theme automatically and should be used for financial data display.