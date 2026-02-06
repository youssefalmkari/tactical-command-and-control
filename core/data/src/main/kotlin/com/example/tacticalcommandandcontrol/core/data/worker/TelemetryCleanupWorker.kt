package com.example.tacticalcommandandcontrol.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tacticalcommandandcontrol.core.common.Constants
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.Duration
import java.time.Instant

@HiltWorker
class TelemetryCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val telemetryDao: TelemetryDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cutoff = Instant.now()
            .minus(Duration.ofHours(Constants.Telemetry.CLEANUP_RETENTION_HOURS))
            .toEpochMilli()

        val countBefore = telemetryDao.count()
        telemetryDao.deleteOlderThan(cutoff)
        val countAfter = telemetryDao.count()

        Timber.d("Telemetry cleanup: removed ${countBefore - countAfter} records (kept $countAfter)")
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "telemetry_cleanup"
    }
}
