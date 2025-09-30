package org.poc.app.shared.business.domain

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.math.round

/**
 * Domain abstraction for precise decimal arithmetic
 * Pure domain concept - no external library dependencies
 */
@Serializable
@JvmInline
value class PreciseDecimal private constructor(private val stringValue: String) : Comparable<PreciseDecimal> {

    companion object {
        val ZERO = PreciseDecimal("0")
        val ONE = PreciseDecimal("1")

        /**
         * Creates PreciseDecimal from string value
         */
        fun fromString(value: String?): PreciseDecimal {
            if (value.isNullOrBlank()) return ZERO
            return try {
                // Basic validation - keep it simple in domain
                val trimmed = value.trim()
                if (trimmed.matches(Regex("-?\\d+(\\.\\d+)?"))) {
                    PreciseDecimal(trimmed)
                } else {
                    ZERO
                }
            } catch (_: Exception) {
                ZERO
            }
        }

        /**
         * Creates PreciseDecimal from double (for compatibility)
         */
        fun fromDouble(value: Double): PreciseDecimal {
            return if (value.isFinite()) {
                PreciseDecimal(value.toString())
            } else {
                ZERO
            }
        }
    }

    /**
     * Convert to Double for UI display
     */
    fun toDouble(): Double {
        return stringValue.toDoubleOrNull() ?: 0.0
    }

    /**
     * Get string representation
     */
    fun toStringValue(): String = stringValue

    /**
     * Format for display with specified decimal places
     */
    fun toDisplayString(decimalPlaces: Int = 8): String {
        val double = toDouble()

        // Handle special cases
        if (!double.isFinite()) return "0"

        // Use proper rounding and formatting
        val multiplier = 10.0.pow(decimalPlaces)
        val rounded = round(double * multiplier) / multiplier

        // Simple decimal formatting without String.format (KMP compatibility)
        return if (decimalPlaces == 0) {
            rounded.toLong().toString()
        } else {
            val str = rounded.toString()
            val decimalIndex = str.indexOf('.')
            if (decimalIndex == -1) {
                // No decimal point, add zeros
                str + "." + "0".repeat(decimalPlaces)
            } else {
                val currentDecimals = str.length - decimalIndex - 1
                when {
                    currentDecimals == decimalPlaces -> str
                    currentDecimals < decimalPlaces -> str + "0".repeat(decimalPlaces - currentDecimals)
                    else -> str.substring(0, decimalIndex + decimalPlaces + 1)
                }
            }
        }
    }

    /**
     * Basic comparison (delegates to Double for simplicity)
     */
    override fun compareTo(other: PreciseDecimal): Int {
        return toDouble().compareTo(other.toDouble())
    }

    fun isZero(): Boolean = stringValue == "0" || stringValue == "0.0"
    fun isPositive(): Boolean = toDouble() > 0.0
    fun isNegative(): Boolean = toDouble() < 0.0

    /**
     * Arithmetic operators for basic operations
     */
    operator fun plus(other: PreciseDecimal): PreciseDecimal {
        val result = toDouble() + other.toDouble()
        return fromDouble(result)
    }

    operator fun minus(other: PreciseDecimal): PreciseDecimal {
        val result = toDouble() - other.toDouble()
        return fromDouble(result)
    }

    operator fun times(other: PreciseDecimal): PreciseDecimal {
        val result = toDouble() * other.toDouble()
        return fromDouble(result)
    }

    operator fun div(other: PreciseDecimal): PreciseDecimal {
        require(!other.isZero()) { "Division by zero" }
        val result = toDouble() / other.toDouble()
        return fromDouble(result)
    }

    operator fun unaryMinus(): PreciseDecimal {
        return fromDouble(-toDouble())
    }

    override fun toString(): String = stringValue
}