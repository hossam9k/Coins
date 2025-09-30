package org.poc.app.shared.business.data

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.poc.app.shared.business.domain.PreciseDecimal

/**
 * Infrastructure layer - handles precise decimal operations using external library
 * Implements domain operations with BigDecimal for actual precision
 */
object BigDecimalOperations {

    /**
     * Performs precise arithmetic operations using BigDecimal
     */
    fun add(a: PreciseDecimal, b: PreciseDecimal): PreciseDecimal {
        val bigA = BigDecimal.parseString(a.toStringValue())
        val bigB = BigDecimal.parseString(b.toStringValue())
        val result = bigA.add(bigB)
        return PreciseDecimal.fromString(result.toStringExpanded())
    }

    fun subtract(a: PreciseDecimal, b: PreciseDecimal): PreciseDecimal {
        val bigA = BigDecimal.parseString(a.toStringValue())
        val bigB = BigDecimal.parseString(b.toStringValue())
        val result = bigA.subtract(bigB)
        return PreciseDecimal.fromString(result.toStringExpanded())
    }

    fun multiply(a: PreciseDecimal, b: PreciseDecimal): PreciseDecimal {
        val bigA = BigDecimal.parseString(a.toStringValue())
        val bigB = BigDecimal.parseString(b.toStringValue())
        val result = bigA.multiply(bigB)
        return PreciseDecimal.fromString(result.toStringExpanded())
    }

    fun divide(a: PreciseDecimal, b: PreciseDecimal): PreciseDecimal {
        require(!b.isZero()) { "Division by zero" }
        val bigA = BigDecimal.parseString(a.toStringValue())
        val bigB = BigDecimal.parseString(b.toStringValue())
        val result = bigA.divide(bigB)
        return PreciseDecimal.fromString(result.toStringExpanded())
    }

    /**
     * Calculate percentage change with precision
     */
    fun percentageChange(current: PreciseDecimal, previous: PreciseDecimal): PreciseDecimal {
        if (previous.isZero()) return PreciseDecimal.ZERO
        val difference = subtract(current, previous)
        val ratio = divide(difference, previous)
        return multiply(ratio, PreciseDecimal.fromString("100"))
    }
}