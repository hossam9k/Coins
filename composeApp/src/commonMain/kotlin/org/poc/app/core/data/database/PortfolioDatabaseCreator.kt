package org.poc.app.core.data.database

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Room database constructor for KMP.
 *
 * IMPORTANT: The "No actual for expect" warning is EXPECTED and SAFE.
 * Room's KSP processor automatically generates platform-specific
 * implementations at compile time.
 *
 * DO NOT create manual actual implementations - let Room handle it.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinRedundantDiagnosticSuppress")
expect object PortfolioDatabaseCreator : RoomDatabaseConstructor<PortfolioDatabase> {
    override fun initialize(): PortfolioDatabase
}

fun getPortfolioDatabase(builder: RoomDatabase.Builder<PortfolioDatabase>): PortfolioDatabase =
    builder
        // .addMigrations(MIGRATIONS)
        // .fallbackToDestructiveMigrationOnDowngrade()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
