package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import androidx.work.ListenableWorker
import com.ivianuu.essentials.coroutines.runWithCleanup
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.work.Worker
import com.ivianuu.essentials.work.WorkerId
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

@Given
object ForegroundWorkerId : WorkerId("foreground")

@Given
fun foregroundWorker(
    @Given internalForegroundState: Flow<InternalForegroundState>,
    @Given notificationManager: @SystemService NotificationManager,
    @Given logger: Logger
): Worker<ForegroundWorkerId> = {
    logger.d { "start foreground worker" }

    suspend fun applyState(infos: List<ForegroundInfo>) {
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

    runWithCleanup(
        block = {
            internalForegroundState
                .map { it.infos }
                .collect { infos ->
                    if (infos.none { it.state is ForegroundState.Foreground }) {
                        throw CancellationException()
                    }
                    applyState(infos)
                }
        },
        cleanup = {
            applyState(emptyList())
        }
    )

    logger.d { "stop foreground worker" }

    ListenableWorker.Result.success()
}
