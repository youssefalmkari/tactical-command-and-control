package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.tacticalcommandandcontrol.core.database.C2Database
import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryDaoTest {

    private lateinit var database: C2Database
    private lateinit var telemetryDao: TelemetryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            C2Database::class.java,
        ).allowMainThreadQueries().build()
        telemetryDao = database.telemetryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createTelemetry(droneId: String, timestamp: Long) = TelemetryEntity(
        droneId = droneId,
        latitude = 37.7749,
        longitude = -122.4194,
        altitudeMsl = 100.0,
        relativeAltitude = 50.0,
        heading = 90.0,
        groundSpeed = 15.0,
        rollDeg = 0.0,
        pitchDeg = 0.0,
        yawDeg = 90.0,
        batteryRemainingPercent = 80,
        batteryVoltageMillivolts = 22000,
        batteryCurrentMilliamps = 14000,
        batteryTemperatureCelsius = 35.0,
        flightMode = "AUTO",
        timestampEpochMillis = timestamp,
    )

    @Test
    fun insertAndObserveLatest() = runTest {
        telemetryDao.insert(createTelemetry("d1", 1000))
        telemetryDao.insert(createTelemetry("d1", 2000))
        telemetryDao.insert(createTelemetry("d1", 3000))

        telemetryDao.observeLatest("d1").test {
            val latest = awaitItem()
            assertNotNull(latest)
            assertEquals(3000L, latest!!.timestampEpochMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeLatestReturnsNullForMissing() = runTest {
        telemetryDao.observeLatest("nonexistent").test {
            val result = awaitItem()
            assertNull(result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeAllLatestReturnsOnePerDrone() = runTest {
        telemetryDao.insert(createTelemetry("d1", 1000))
        telemetryDao.insert(createTelemetry("d1", 2000))
        telemetryDao.insert(createTelemetry("d2", 1500))
        telemetryDao.insert(createTelemetry("d2", 2500))

        telemetryDao.observeAllLatest().test {
            val results = awaitItem()
            assertEquals(2, results.size)

            val d1 = results.find { it.droneId == "d1" }
            val d2 = results.find { it.droneId == "d2" }
            assertNotNull(d1)
            assertNotNull(d2)
            assertEquals(2000L, d1!!.timestampEpochMillis)
            assertEquals(2500L, d2!!.timestampEpochMillis)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteOlderThanRemovesOldRecords() = runTest {
        telemetryDao.insert(createTelemetry("d1", 1000))
        telemetryDao.insert(createTelemetry("d1", 2000))
        telemetryDao.insert(createTelemetry("d1", 3000))

        telemetryDao.deleteOlderThan(2500)

        val count = telemetryDao.count()
        assertEquals(1, count)
    }

    @Test
    fun countReturnsCorrectNumber() = runTest {
        assertEquals(0, telemetryDao.count())

        telemetryDao.insertAll(
            listOf(
                createTelemetry("d1", 1000),
                createTelemetry("d2", 2000),
            ),
        )

        assertEquals(2, telemetryDao.count())
    }
}
