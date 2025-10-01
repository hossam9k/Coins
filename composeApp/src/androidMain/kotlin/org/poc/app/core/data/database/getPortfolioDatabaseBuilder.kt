package org.poc.app.core.domain.utilbase

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.poc.app.core.data.database.PortfolioDatabase

fun getPortfolioDatabaseBuilder(context: Context): RoomDatabase.Builder<PortfolioDatabase> {
    val dbFile = context.getDatabasePath("portfolio.db")
    return Room.databaseBuilder<PortfolioDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}