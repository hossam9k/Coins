package org.poc.app.shared.business.util

import org.poc.app.shared.business.domain.PreciseDecimal

/**
 * Banking-grade formatters for fintech applications
 * Follows international financial display standards
 *
 * FINTECH FORMATTING STANDARDS:
 * =============================
 *
 * FIAT CURRENCY:
 * - $1,234,567.89    (millions with commas)
 * - $12,345.67       (thousands with commas)
 * - $123.45          (regular amounts, 2 decimals)
 * - $0.12            (cents, always 2 decimals)
 * - -$123.45         (negative amounts with proper sign)
 *
 * CRYPTOCURRENCY:
 * - 1,234.12345678 BTC    (high precision, remove trailing zeros)
 * - 0.00012345 ETH        (micro amounts, up to 8 decimals)
 *
 * PERCENTAGES:
 * - +12.34%          (positive gains with + sign)
 * - -5.67%           (negative losses with - sign)
 * - 0.00%            (no change)
 *
 * TRANSACTION AMOUNTS:
 * - +$1,234.56       (credits with + sign)
 * - -$1,234.56       (debits with - sign)
 *
 * PRECISION RULES:
 * - Use BigDecimal arithmetic internally
 * - Round only at display time
 * - Maintain precision for calculations
 * - Follow banking rounding rules (round half up)
 *
 * ARABIC/RTL SUPPORT:
 * - ١٢٣,٤٥٦.٧٨ ر.س    (Arabic-Indic numerals)
 * - 123,456.78 ر.س      (Western numerals with Arabic currency)
 * - Proper RTL currency symbol placement
 * - Thousands separator: comma (,) or Arabic comma (،)
 * - Decimal separator: dot (.) or Arabic decimal separator (٫)
 */

/**
 * Format fiat currency with banking standards
 * - Always show 2 decimal places for fiat
 * - Use thousands separators for large amounts
 * - Handle negative amounts properly
 */
fun formatFiatPrecise(amount: PreciseDecimal, currencySymbol: String = "$", showDecimal: Boolean = true): String {
    val absAmount = if (amount.isNegative()) PreciseDecimal.fromString("0") - amount else amount
    val sign = if (amount.isNegative()) "-" else ""

    return when {
        !showDecimal -> {
            val formatted = formatWithCommas(absAmount.toDisplayString(0))
            "$sign$currencySymbol$formatted"
        }
        else -> {
            val formatted = formatWithCommas(absAmount.toDisplayString(2))
            "$sign$currencySymbol$formatted"
        }
    }
}

/**
 * Format cryptocurrency amounts with appropriate precision
 * - 8 decimal places for most cryptocurrencies
 * - Remove trailing zeros for cleaner display
 * - Add thousands separators for large amounts
 */
fun formatCryptoPrecise(amount: PreciseDecimal, symbol: String, decimals: Int = 8): String {
    val formatted = amount.toDisplayString(decimals).trimEnd('0').trimEnd('.')
    val withCommas = formatWithCommas(formatted)
    return "$withCommas $symbol"
}

/**
 * Format percentage with banking precision
 * - Always 2 decimal places
 * - Proper sign handling
 * - Consistent spacing
 */
fun formatPercentagePrecise(amount: PreciseDecimal): String {
    val formatted = amount.toDisplayString(2)
    val sign = if (!amount.isNegative() && !amount.isZero()) "+" else ""
    return "$sign$formatted%"
}

/**
 * Banking-grade price display formatter
 * Follows financial industry standards for price representation
 */
fun formatPriceDisplay(price: PreciseDecimal, currencySymbol: String = "$"): String {
    val absPrice = if (price.isNegative()) PreciseDecimal.ZERO - price else price
    val sign = if (price.isNegative()) "-" else ""

    return when {
        absPrice >= PreciseDecimal.fromString("1000000") -> {
            // Millions: $1,234,567.89
            val formatted = formatWithCommas(absPrice.toDisplayString(2))
            "$sign$currencySymbol$formatted"
        }
        absPrice >= PreciseDecimal.fromString("1000") -> {
            // Thousands: $1,234.56
            val formatted = formatWithCommas(absPrice.toDisplayString(2))
            "$sign$currencySymbol$formatted"
        }
        absPrice >= PreciseDecimal.fromString("1") -> {
            // Regular amounts: $123.45
            "$sign$currencySymbol${absPrice.toDisplayString(2)}"
        }
        absPrice >= PreciseDecimal.fromString("0.01") -> {
            // Cents: $0.12
            "$sign$currencySymbol${absPrice.toDisplayString(2)}"
        }
        absPrice >= PreciseDecimal.fromString("0.0001") -> {
            // Sub-cent with 4 decimals: $0.0123
            "$sign$currencySymbol${absPrice.toDisplayString(4)}"
        }
        else -> {
            // Micro amounts with 8 decimals: $0.00012345
            val formatted = absPrice.toDisplayString(8).trimEnd('0').trimEnd('.')
            "$sign$currencySymbol$formatted"
        }
    }
}

/**
 * Add thousand separators to number string
 */
private fun formatWithCommas(numberString: String): String {
    val parts = numberString.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) ".${parts[1]}" else ""

    // Add commas to integer part
    val formattedInteger = integerPart.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    return formattedInteger + decimalPart
}

/**
 * Banking-grade percentage change formatter
 * Includes proper color coding hints and sign handling
 */
fun formatChangeDisplay(change: PreciseDecimal): String {
    val formatted = change.toDisplayString(2)
    return when {
        change.isPositive() -> "+$formatted%"
        change.isNegative() -> "$formatted%" // Already has negative sign
        else -> "$formatted%" // Zero
    }
}

/**
 * Format balance for account displays
 * Professional banking balance formatting
 */
fun formatBalanceDisplay(balance: PreciseDecimal, currencySymbol: String = "$"): String {
    return formatPriceDisplay(balance, currencySymbol)
}

/**
 * Format transaction amounts (with explicit signs)
 * Used in transaction history and statements
 */
fun formatTransactionAmount(amount: PreciseDecimal, currencySymbol: String = "$"): String {
    val absAmount = if (amount.isNegative()) PreciseDecimal.ZERO - amount else amount
    val sign = when {
        amount.isPositive() -> "+"
        amount.isNegative() -> "-"
        else -> ""
    }

    val formatted = when {
        absAmount >= PreciseDecimal.fromString("1000") -> {
            formatWithCommas(absAmount.toDisplayString(2))
        }
        else -> {
            absAmount.toDisplayString(2)
        }
    }

    return "$sign$currencySymbol$formatted"
}

/**
 * Convert precise decimal list to doubles for chart visualization
 * This is acceptable since charts are for visual representation only
 */
fun List<PreciseDecimal>.toChartData(): List<Double> {
    return map { it.toDouble() }
}

/**
 * International price formatter with locale support
 * Automatically detects and applies appropriate formatting
 */
fun formatPriceInternational(
    price: PreciseDecimal,
    localeCode: String = "en-US"
): String {
    return when {
        localeCode.startsWith("ar") -> formatPriceWithLocale(price, localeCode)
        else -> formatPriceDisplay(price)
    }
}

/**
 * International percentage formatter with locale support
 */
fun formatPercentageInternational(
    change: PreciseDecimal,
    localeCode: String = "en-US"
): String {
    return when {
        localeCode.startsWith("ar") -> formatPercentageArabic(change,
            when (localeCode.lowercase()) {
                "ar-sa", "ar_sa" -> ArabicLocales.SAUDI_ARABIA
                "ar-ae", "ar_ae" -> ArabicLocales.UAE
                "ar-eg", "ar_eg" -> ArabicLocales.EGYPT
                "ar-kw", "ar_kw" -> ArabicLocales.KUWAIT
                else -> LocaleConfig(isRTL = true)
            }
        )
        else -> formatChangeDisplay(change)
    }
}