package org.poc.app.core.domain.util

import org.poc.app.core.domain.model.PreciseDecimal

/**
 * Arabic/RTL number formatting for fintech applications
 * Supports both Arabic-Indic numerals and Western numerals with Arabic currencies
 */

enum class NumberSystem {
    WESTERN, // 0123456789
    ARABIC_INDIC, // ٠١٢٣٤٥٦٧٨٩
}

enum class CurrencyPosition {
    PREFIX, // $123.45
    SUFFIX, // 123.45 ر.س
}

data class LocaleConfig(
    val numberSystem: NumberSystem = NumberSystem.WESTERN,
    val currencyPosition: CurrencyPosition = CurrencyPosition.PREFIX,
    val thousandsSeparator: String = ",", // or "،" for Arabic comma
    val decimalSeparator: String = ".", // or "٫" for Arabic decimal
    val currencySymbol: String = "$",
    val isRTL: Boolean = false,
)

/**
 * Arabic locale configurations for Middle Eastern countries
 */
object ArabicLocales {
    val SAUDI_ARABIA =
        LocaleConfig(
            numberSystem = NumberSystem.WESTERN,
            currencyPosition = CurrencyPosition.SUFFIX,
            thousandsSeparator = ",",
            decimalSeparator = ".",
            currencySymbol = "ر.س",
            isRTL = true,
        )

    val UAE =
        LocaleConfig(
            numberSystem = NumberSystem.WESTERN,
            currencyPosition = CurrencyPosition.SUFFIX,
            thousandsSeparator = ",",
            decimalSeparator = ".",
            currencySymbol = "د.إ",
            isRTL = true,
        )

    val EGYPT =
        LocaleConfig(
            numberSystem = NumberSystem.ARABIC_INDIC,
            currencyPosition = CurrencyPosition.SUFFIX,
            thousandsSeparator = "،", // Arabic comma
            decimalSeparator = "٫", // Arabic decimal separator
            currencySymbol = "ج.م",
            isRTL = true,
        )

    val KUWAIT =
        LocaleConfig(
            numberSystem = NumberSystem.WESTERN,
            currencyPosition = CurrencyPosition.SUFFIX,
            thousandsSeparator = ",",
            decimalSeparator = ".",
            currencySymbol = "د.ك",
            isRTL = true,
        )
}

/**
 * Convert Western digits to Arabic-Indic digits
 */
fun String.toArabicIndic(): String {
    val arabicDigits =
        mapOf(
            '0' to '٠',
            '1' to '١',
            '2' to '٢',
            '3' to '٣',
            '4' to '٤',
            '5' to '٥',
            '6' to '٦',
            '7' to '٧',
            '8' to '٨',
            '9' to '٩',
        )
    return this.map { arabicDigits[it] ?: it }.joinToString("")
}

/**
 * Format number according to locale configuration
 */
fun formatNumberForLocale(
    number: String,
    config: LocaleConfig,
): String {
    val converted =
        when (config.numberSystem) {
            NumberSystem.WESTERN -> number
            NumberSystem.ARABIC_INDIC -> number.toArabicIndic()
        }

    // Replace separators
    return converted
        .replace(",", config.thousandsSeparator)
        .replace(".", config.decimalSeparator)
}

/**
 * Format currency amount with Arabic/RTL support
 */
fun formatCurrencyArabic(
    amount: PreciseDecimal,
    config: LocaleConfig = LocaleConfig(),
): String {
    val absAmount = if (amount.isNegative()) -amount else amount
    val sign = if (amount.isNegative()) "-" else ""

    val formattedNumber =
        when {
            absAmount >= PreciseDecimal.fromString("1000") -> {
                formatWithCommas(absAmount.toDisplayString(2))
            }
            else -> {
                absAmount.toDisplayString(2)
            }
        }

    val localizedNumber = formatNumberForLocale(formattedNumber, config)

    return when (config.currencyPosition) {
        CurrencyPosition.PREFIX -> {
            if (config.isRTL) {
                // RTL: number currency sign
                "$localizedNumber ${config.currencySymbol}$sign"
            } else {
                // LTR: sign currency number
                "$sign${config.currencySymbol}$localizedNumber"
            }
        }
        CurrencyPosition.SUFFIX -> {
            if (config.isRTL) {
                // RTL: sign currency number (visual order due to RTL)
                "$sign${config.currencySymbol} $localizedNumber"
            } else {
                // LTR: sign number currency
                "$sign$localizedNumber ${config.currencySymbol}"
            }
        }
    }
}

/**
 * Format percentage with Arabic support
 */
fun formatPercentageArabic(
    change: PreciseDecimal,
    config: LocaleConfig = LocaleConfig(),
): String {
    val formatted = change.toDisplayString(2)
    val localizedNumber = formatNumberForLocale(formatted, config)

    val sign =
        when {
            change.isPositive() -> "+"
            change.isNegative() -> "" // Already included in number
            else -> ""
        }

    return if (config.isRTL) {
        "%$localizedNumber$sign" // RTL: % comes first
    } else {
        "$sign$localizedNumber%"
    }
}

/**
 * Add thousand separators to number string (shared with PreciseFormatter)
 */
private fun formatWithCommas(numberString: String): String {
    val parts = numberString.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) ".${parts[1]}" else ""

    // Add commas to integer part
    val formattedInteger =
        integerPart
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

    return formattedInteger + decimalPart
}

/**
 * Smart formatter that detects locale and applies appropriate formatting
 */
fun formatPriceWithLocale(
    price: PreciseDecimal,
    localeCode: String = "en",
): String {
    val config =
        when (localeCode.lowercase()) {
            "ar-sa", "ar_sa" -> ArabicLocales.SAUDI_ARABIA
            "ar-ae", "ar_ae" -> ArabicLocales.UAE
            "ar-eg", "ar_eg" -> ArabicLocales.EGYPT
            "ar-kw", "ar_kw" -> ArabicLocales.KUWAIT
            else -> LocaleConfig() // Default English
        }

    return formatCurrencyArabic(price, config)
}
