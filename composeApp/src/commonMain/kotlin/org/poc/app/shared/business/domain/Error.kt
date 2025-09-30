package org.poc.app.shared.business.domain

/**
 * Base marker interface for all domain errors.
 * Provides type safety for Result<T, E> and enables polymorphic error handling.
 *
 * Usage: Create sealed interfaces extending Error for error categories.
 */
interface Error