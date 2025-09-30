package org.poc.app.shared.business.domain

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * Precise decimal price handling for financial applications
 * Uses BigDecimal internally for exact arithmetic
 */
@Serializable(with = DecimalPriceSerializer::class)
@JvmInline
value class DecimalPrice private constructor(private val value: BigDecimal) : Comparable<DecimalPrice> {

    companion object {
        val ZERO = DecimalPrice(BigDecimal.ZERO)
        val ONE = DecimalPrice(BigDecimal.ONE)

        /**
         * Creates DecimalPrice from string (recommended for API data)
         */
        fun fromString(value: String?): DecimalPrice {
            if (value.isNullOrBlank()) return ZERO
            return try {
                DecimalPrice(BigDecimal.parseString(value.trim()))
            } catch (_: Exception) {
                ZERO
            }
        }

        /**
         * Creates DecimalPrice from double (use for existing code compatibility)
         */
        fun fromDouble(value: Double): DecimalPrice {
            if (!value.isFinite()) return ZERO
            return DecimalPrice(BigDecimal.fromDouble(value))
        }
    }

    // Conversion methods
    fun toDouble(): Double = value.doubleValue(exactRequired = false)

    fun toDisplayString(scale: Int = 8): String {
        return value.scale(scale.toLong()).toStringExpanded()
    }

    // Arithmetic operations
    operator fun plus(other: DecimalPrice) = DecimalPrice(value.add(other.value))
    operator fun minus(other: DecimalPrice) = DecimalPrice(value.subtract(other.value))
    operator fun times(other: DecimalPrice) = DecimalPrice(value.multiply(other.value))

    operator fun div(other: DecimalPrice): DecimalPrice {
        require(!other.isZero()) { "Division by zero" }
        return DecimalPrice(value.divide(other.value))
    }

    // Comparison
    override fun compareTo(other: DecimalPrice): Int = value.compare(other.value)

    fun isZero(): Boolean = value.isZero()
    fun isPositive(): Boolean = this > ZERO
    fun isNegative(): Boolean = this < ZERO

    override fun toString(): String = toDisplayString()
}

/**
 * Custom serializer for DecimalPrice to store as string for precision
 */
object DecimalPriceSerializer : KSerializer<DecimalPrice> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DecimalPrice", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DecimalPrice) {
        encoder.encodeString(value.toDisplayString(18))
    }

    override fun deserialize(decoder: Decoder): DecimalPrice {
        return DecimalPrice.fromString(decoder.decodeString())
    }
}