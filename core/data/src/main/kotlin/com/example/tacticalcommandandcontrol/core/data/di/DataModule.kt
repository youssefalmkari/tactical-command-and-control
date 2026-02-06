package com.example.tacticalcommandandcontrol.core.data.di

import com.example.tacticalcommandandcontrol.core.data.repository.DroneRepositoryImpl
import com.example.tacticalcommandandcontrol.core.data.repository.MissionRepositoryImpl
import com.example.tacticalcommandandcontrol.core.data.repository.TelemetryRepositoryImpl
import com.example.tacticalcommandandcontrol.core.domain.repository.DroneRepository
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import com.example.tacticalcommandandcontrol.core.domain.repository.TelemetryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindDroneRepository(impl: DroneRepositoryImpl): DroneRepository

    @Binds
    @Singleton
    abstract fun bindMissionRepository(impl: MissionRepositoryImpl): MissionRepository

    @Binds
    @Singleton
    abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository
}
