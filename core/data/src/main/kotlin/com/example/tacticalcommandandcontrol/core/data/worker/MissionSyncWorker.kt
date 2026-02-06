package com.example.tacticalcommandandcontrol.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class MissionSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val missionRepository: MissionRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Timber.d("Starting mission sync (attempt $runAttemptCount)")

        return missionRepository.syncMissions().fold(
            onSuccess = {
                Timber.d("Mission sync completed successfully")
                Result.success()
            },
            onFailure = { error ->
                Timber.e(error, "Mission sync failed")
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            },
        )
    }

    companion object {
        const val WORK_NAME = "mission_sync"
        private const val MAX_RETRY_ATTEMPTS = 5
    }
}
