package org.poc.app.shared.business.util

import org.poc.app.shared.business.domain.PreciseDecimal

/**
 * Test functions to validate Arabic formatting functionality
 * This file demonstrates how the Arabic formatting works with sample data
 */

// Test data constants
private object TestData {
    val TEST_AMOUNT = PreciseDecimal.fromString("12345.67")
    val LARGE_AMOUNT = PreciseDecimal.fromString("1234567.89")
    val SMALL_AMOUNT = PreciseDecimal.fromString("123.45")
    val PERCENTAGE_CHANGE = PreciseDecimal.fromString("5.67")
    val NEGATIVE_CHANGE = PreciseDecimal.fromString("-3.45")
}

fun testArabicFormatting() {
    val testAmount = TestData.TEST_AMOUNT
    val largeAmount = TestData.LARGE_AMOUNT
    val smallAmount = TestData.SMALL_AMOUNT
    val percentageChange = TestData.PERCENTAGE_CHANGE
    val negativeChange = TestData.NEGATIVE_CHANGE

    println("=== Arabic Formatting Test ===")

    // Test Saudi Arabia formatting
    println("\n--- Saudi Arabia (ر.س) ---")
    println("Small amount: ${formatCurrencyArabic(smallAmount, ArabicLocales.SAUDI_ARABIA)}")
    println("Large amount: ${formatCurrencyArabic(largeAmount, ArabicLocales.SAUDI_ARABIA)}")
    println("Percentage +: ${formatPercentageArabic(percentageChange, ArabicLocales.SAUDI_ARABIA)}")
    println("Percentage -: ${formatPercentageArabic(negativeChange, ArabicLocales.SAUDI_ARABIA)}")

    // Test UAE formatting
    println("\n--- UAE (د.إ) ---")
    println("Small amount: ${formatCurrencyArabic(smallAmount, ArabicLocales.UAE)}")
    println("Large amount: ${formatCurrencyArabic(largeAmount, ArabicLocales.UAE)}")

    // Test Egypt formatting (Arabic-Indic numerals)
    println("\n--- Egypt (ج.م) - Arabic-Indic Numerals ---")
    println("Small amount: ${formatCurrencyArabic(smallAmount, ArabicLocales.EGYPT)}")
    println("Large amount: ${formatCurrencyArabic(largeAmount, ArabicLocales.EGYPT)}")
    println("Percentage +: ${formatPercentageArabic(percentageChange, ArabicLocales.EGYPT)}")

    // Test Kuwait formatting
    println("\n--- Kuwait (د.ك) ---")
    println("Small amount: ${formatCurrencyArabic(smallAmount, ArabicLocales.KUWAIT)}")
    println("Large amount: ${formatCurrencyArabic(largeAmount, ArabicLocales.KUWAIT)}")

    // Test international formatting with locale codes
    println("\n--- International Formatting ---")
    println("en-US: ${formatPriceInternational(testAmount, "en-US")}")
    println("ar-SA: ${formatPriceInternational(testAmount, "ar-SA")}")
    println("ar-EG: ${formatPriceInternational(testAmount, "ar-EG")}")
    println("ar-AE: ${formatPriceInternational(testAmount, "ar-AE")}")

    // Test percentage international formatting
    println("\n--- International Percentage Formatting ---")
    println("en-US +: ${formatPercentageInternational(percentageChange, "en-US")}")
    println("ar-SA +: ${formatPercentageInternational(percentageChange, "ar-SA")}")
    println("ar-EG -: ${formatPercentageInternational(negativeChange, "ar-EG")}")
}

/**
 * Test Arabic-Indic numeral conversion
 */
fun testArabicIndicNumerals() {
    println("\n=== Arabic-Indic Numeral Conversion Test ===")

    val testNumbers = listOf("123.45", "1,234.67", "1,234,567.89", "-123.45")

    testNumbers.forEach { number ->
        val converted = number.toArabicIndic()
        println("$number -> $converted")
    }

    // Test with real formatting
    val amount = TestData.TEST_AMOUNT
    val arabicFormatted = formatCurrencyArabic(amount, ArabicLocales.EGYPT)

    println("\nFormatted amount:")
    println("Amount: ${amount.toDisplayString(2)}")
    println("Arabic-Indic formatted: $arabicFormatted")

    // Validate individual digit conversion
    println("\nDigit-by-digit conversion:")
    for (i in 0..9) {
        val western = i.toString()
        val arabic = western.toArabicIndic()
        println("$western -> $arabic")
    }

    // Test edge cases
    println("\nEdge cases:")
    val emptyString = ""
    val mixedText = "abc123def"
    val punctuation = ".,()-"
    println("Empty string: '${emptyString.toArabicIndic()}'")
    println("Mixed text: '${mixedText.toArabicIndic()}'")
    println("Only punctuation: '${punctuation.toArabicIndic()}'")
}

/**
 * Validate that all Arabic locale configurations work correctly
 */
fun validateLocaleConfigurations() {
    println("\n=== Locale Configuration Validation ===")

    val testAmount = TestData.TEST_AMOUNT
    val negativeAmount = -TestData.TEST_AMOUNT

    println("\n1. Saudi Arabia (ر.س) - Western numerals, suffix position:")
    val saPositive = formatCurrencyArabic(testAmount, ArabicLocales.SAUDI_ARABIA)
    val saNegative = formatCurrencyArabic(negativeAmount, ArabicLocales.SAUDI_ARABIA)
    println("Positive: $saPositive")
    println("Negative: $saNegative")
    println("Expected format: 12,345.67 ر.س")

    println("\n2. UAE (د.إ) - Western numerals, suffix position:")
    val uaePositive = formatCurrencyArabic(testAmount, ArabicLocales.UAE)
    val uaeNegative = formatCurrencyArabic(negativeAmount, ArabicLocales.UAE)
    println("Positive: $uaePositive")
    println("Negative: $uaeNegative")
    println("Expected format: 12,345.67 د.إ")

    println("\n3. Egypt (ج.م) - Arabic-Indic numerals, Arabic separators:")
    val egyptPositive = formatCurrencyArabic(testAmount, ArabicLocales.EGYPT)
    val egyptNegative = formatCurrencyArabic(negativeAmount, ArabicLocales.EGYPT)
    println("Positive: $egyptPositive")
    println("Negative: $egyptNegative")
    println("Expected format: ١٢،٣٤٥٫٦٧ ج.م")

    println("\n4. Kuwait (د.ك) - Western numerals, suffix position:")
    val kuwaitPositive = formatCurrencyArabic(testAmount, ArabicLocales.KUWAIT)
    val kuwaitNegative = formatCurrencyArabic(negativeAmount, ArabicLocales.KUWAIT)
    println("Positive: $kuwaitPositive")
    println("Negative: $kuwaitNegative")
    println("Expected format: 12,345.67 د.ك")

    // Test percentage formatting for each locale
    val percentageChange = TestData.PERCENTAGE_CHANGE
    println("\n--- Percentage Formatting by Locale ---")
    println("Saudi Arabia: ${formatPercentageArabic(percentageChange, ArabicLocales.SAUDI_ARABIA)}")
    println("UAE: ${formatPercentageArabic(percentageChange, ArabicLocales.UAE)}")
    println("Egypt: ${formatPercentageArabic(percentageChange, ArabicLocales.EGYPT)}")
    println("Kuwait: ${formatPercentageArabic(percentageChange, ArabicLocales.KUWAIT)}")
}

/**
 * Main function to run all tests
 */
fun runArabicFormatterTests() {
    testArabicFormatting()
    testArabicIndicNumerals()
    validateLocaleConfigurations()
}

/**
 * Expected output examples for documentation:
 *
 * CORRECT FORMATTING EXAMPLES:
 * ============================
 *
 * Saudi Arabia: 12,345.67 ر.س (Western numerals, RTL currency suffix)
 * UAE: 12,345.67 د.إ (Western numerals, RTL currency suffix)
 * Egypt: ١٢،٣٤٥٫٦٧ ج.م (Arabic-Indic numerals with Arabic separators)
 * Kuwait: 12,345.67 د.ك (Western numerals, RTL currency suffix)
 *
 * Negative amounts:
 * Saudi Arabia: -12,345.67 ر.س
 * Egypt: -١٢،٣٤٥٫٦٧ ج.م
 *
 * Percentages (RTL):
 * English: +5.67%
 * Arabic RTL: %٥٫٦٧+ (% comes first in RTL layout)
 *
 * INTERNATIONAL FORMATTING:
 * ========================
 * formatPriceInternational(amount, "en-US") -> $12,345.67
 * formatPriceInternational(amount, "ar-SA") -> 12,345.67 ر.س
 * formatPriceInternational(amount, "ar-EG") -> ١٢،٣٤٥٫٦٧ ج.م
 */