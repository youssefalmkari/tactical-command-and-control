package com.example.tacticalcommandandcontrol.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.tacticalcommandandcontrol.core.database.C2Database
import com.example.tacticalcommandandcontrol.core.database.dao.DroneDao
import com.example.tacticalcommandandcontrol.core.database.dao.MissionDao
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import com.example.tacticalcommandandcontrol.core.database.dao.WaypointDao
import com.example.tacticalcommandandcontrol.core.database.security.EncryptedDatabaseFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): C2Database {
        val passphrase = EncryptedDatabaseFactory.getPassphrase()
        val factory = SupportOpenHelperFactory(passphrase)

        return Room.databaseBuilder(
            context,
            C2Database::class.java,
            C2Database.DATABASE_NAME,
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideDroneDao(database: C2Database): DroneDao = database.droneDao()

    @Provides
    fun provideMissionDao(database: C2Database): MissionDao = database.missionDao()

    @Provides
    fun provideWaypointDao(database: C2Database): WaypointDao = database.waypointDao()

    @Provides
    fun provideTelemetryDao(database: C2Database): TelemetryDao = database.telemetryDao()
}
