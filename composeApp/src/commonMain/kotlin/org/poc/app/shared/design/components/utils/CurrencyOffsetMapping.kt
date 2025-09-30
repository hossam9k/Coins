package org.poc.app.shared.design.components.utils

import androidx.compose.ui.text.input.OffsetMapping

/**
 * Design System Currency Offset Mapping Utility
 *
 * Handles cursor position mapping between original numeric input
 * and formatted currency display text.
 *
 * Used internally by CurrencyVisualTransformation to maintain
 * correct cursor positioning during text input.
 */
class CurrencyOffsetMapping(originalText: String, formattedText: String) : OffsetMapping {
    private val originalLength = originalText.length
    private val indexes = findDigitIndexes(originalText, formattedText)

    private fun findDigitIndexes(firstString: String, secondString: String): List<Int> {
        val digitIndexes = mutableListOf<Int>()
        var currentIndex = 0
        for (digit in firstString) {
            val index = secondString.indexOf(digit, currentIndex)
            if (index != -1) {
                digitIndexes.add(index)
                currentIndex = index + 1
            } else {
                return emptyList()
            }
        }
        return digitIndexes
    }

    override fun originalToTransformed(offset: Int): Int {
        if (offset >= originalLength) {
            return indexes.last() + 1
        }
        return indexes[offset]
    }

    override fun transformedToOriginal(offset: Int): Int {
        return indexes.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
    }
}