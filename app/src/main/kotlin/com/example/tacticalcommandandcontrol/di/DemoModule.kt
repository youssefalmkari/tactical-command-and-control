package com.example.tacticalcommandandcontrol.di

import com.example.tacticalcommandandcontrol.core.database.dao.DroneDao
import com.example.tacticalcommandandcontrol.core.database.dao.MissionDao
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import com.example.tacticalcommandandcontrol.core.database.dao.WaypointDao
import com.example.tacticalcommandandcontrol.demo.DemoSimulator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DemoScope

@Module
@InstallIn(SingletonComponent::class)
object DemoModule {

    @Provides
    @Singleton
    @DemoScope
    fun provideDemoScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideDemoSimulator(
        droneDao: DroneDao,
        telemetryDao: TelemetryDao,
        missionDao: MissionDao,
        waypointDao: WaypointDao,
        @DemoScope scope: CoroutineScope,
    ): DemoSimulator = DemoSimulator(
        droneDao = droneDao,
        telemetryDao = telemetryDao,
        missionDao = missionDao,
        waypointDao = waypointDao,
        scope = scope,
    )
}
