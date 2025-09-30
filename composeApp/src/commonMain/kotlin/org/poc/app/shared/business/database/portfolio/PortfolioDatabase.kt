package org.poc.app.shared.business.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import org.poc.app.feature.portfolio.data.local.PortfolioCoinEntity
import org.poc.app.feature.portfolio.data.local.PortfolioDao
import org.poc.app.feature.portfolio.data.local.UserBalanceDao
import org.poc.app.feature.portfolio.data.local.UserBalanceEntity


@ConstructedBy(PortfolioDatabaseCreator::class)
@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 2)
abstract class PortfolioDatabase: RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
}