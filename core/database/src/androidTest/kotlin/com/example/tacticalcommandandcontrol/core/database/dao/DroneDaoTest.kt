package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.tacticalcommandandcontrol.core.database.C2Database
import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DroneDaoTest {

    private lateinit var database: C2Database
    private lateinit var droneDao: DroneDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            C2Database::class.java,
        ).allowMainThreadQueries().build()
        droneDao = database.droneDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createDrone(id: String, name: String = "Drone $id") = DroneEntity(
        id = id,
        name = name,
        type = "Quadcopter",
        status = "IDLE",
        flightMode = "MANUAL",
        latitude = null,
        longitude = null,
        altitudeMsl = null,
        relativeAltitude = null,
        heading = null,
        groundSpeed = null,
        rollDeg = null,
        pitchDeg = null,
        yawDeg = null,
        batteryRemainingPercent = null,
        batteryVoltageMillivolts = null,
        batteryCurrentMilliamps = null,
        batteryTemperatureCelsius = null,
        isConnected = false,
        lastSeenEpochMillis = System.currentTimeMillis(),
    )

    @Test
    fun upsertAndGetById() = runTest {
        val drone = createDrone("d1")
        droneDao.upsert(drone)

        val result = droneDao.getById("d1")
        assertNotNull(result)
        assertEquals("d1", result!!.id)
    }

    @Test
    fun getByIdReturnsNullForMissing() = runTest {
        val result = droneDao.getById("nonexistent")
        assertNull(result)
    }

    @Test
    fun upsertUpdatesExisting() = runTest {
        droneDao.upsert(createDrone("d1", name = "Alpha"))
        droneDao.upsert(createDrone("d1", name = "Alpha Updated"))

        val result = droneDao.getById("d1")
        assertEquals("Alpha Updated", result!!.name)
    }

    @Test
    fun observeAllReturnsSortedByName() = runTest {
        droneDao.upsert(createDrone("d2", name = "Bravo"))
        droneDao.upsert(createDrone("d1", name = "Alpha"))
        droneDao.upsert(createDrone("d3", name = "Charlie"))

        droneDao.observeAll().test {
            val drones = awaitItem()
            assertEquals(3, drones.size)
            assertEquals("Alpha", drones[0].name)
            assertEquals("Bravo", drones[1].name)
            assertEquals("Charlie", drones[2].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteByIdRemovesDrone() = runTest {
        droneDao.upsert(createDrone("d1"))
        droneDao.deleteById("d1")

        val result = droneDao.getById("d1")
        assertNull(result)
    }

    @Test
    fun deleteAllClearsTable() = runTest {
        droneDao.upsertAll(listOf(createDrone("d1"), createDrone("d2")))
        droneDao.deleteAll()

        droneDao.observeAll().test {
            val drones = awaitItem()
            assertEquals(0, drones.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
