package com.ivianuu.essentials.foreground

import android.app.*
import androidx.work.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.work.*
import com.ivianuu.essentials.work.Worker
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Given object ForegroundWorkerId : WorkerId("foreground")

@Given fun foregroundWorker(
  @Given internalForegroundState: Flow<InternalForegroundState>,
  @Given notificationManager: @SystemService NotificationManager,
  @Given _: Logger
): Worker<ForegroundWorkerId> = {
  d { "start foreground worker" }

  suspend fun applyState(infos: List<ForegroundInfo>) {
    d { "apply infos: $infos" }

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
              ForegroundInfo(
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

  d { "stop foreground worker" }

  ListenableWorker.Result.success()
}
