package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.work.WorkerBinding
import com.ivianuu.injekt.android.work.WorkerContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@WorkerBinding
@Given
class ForegroundWorker(
    @Given appContext: WorkerContext,
    @Given params: WorkerParameters,
    @Given private val internalForegroundState: Flow<InternalForegroundState>,
    @Given private val notificationManager: NotificationManager,
    @Given private val logger: Logger
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        logger.d { "start foreground worker" }

        runWithCleanup(
            block = {
                internalForegroundState
                    .map { it.infos }
                    .onEach { infos ->
                        if (infos.none { it.state is ForegroundState.Foreground }) {
                            throw CancellationException()
                        }
                    }
                    .onEach { applyState(it) }
                    .collect()
            },
            cleanup = {
                applyState(emptyList())
            }
        )

        logger.d { "stop foreground worker" }

        return Result.success()
    }

    private suspend fun applyState(infos: List<ForegroundInfo>) {
        logger.d { "apply infos: $infos" }

        infos
            .filter { it.state is ForegroundState.Background }
            .forEach { notificationManager.cancel(it.id) }

        if (infos.any { it.state is ForegroundState.Foreground }) {
            infos
                .mapNotNull { info ->
                    (info.state as? ForegroundState.Foreground)?.let {
                        info.id to it.notification
                    }
                }
                .forEachIndexed { index, (id, notification) ->
                    if (index == 0) {
                        setForeground(
                            androidx.work.ForegroundInfo(
                                id,
                                notification
                            )
                        )
                    } else {
                        notificationManager.notify(id, notification)
                    }
                }
        }
    }
}
