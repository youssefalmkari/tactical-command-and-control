package com.example.tacticalcommandandcontrol.core.network.di

import com.example.tacticalcommandandcontrol.core.network.mqtt.MqttConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMqttConfig(): MqttConfig = MqttConfig()

    @Provides
    @Singleton
    fun provideNetworkScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
