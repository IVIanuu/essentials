/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.launchComposition
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.util.NotificationFactory
import com.ivianuu.essentials.util.StartAppRemoteAction
import com.ivianuu.essentials.util.context
import com.ivianuu.essentials.util.remoteActionOf
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

@Provide @AndroidComponent class ForegroundService(
  private val appConfig: AppConfig,
  private val foregroundManager: ForegroundManagerImpl,
  @Inject private val json: Json,
  private val notificationFactory: NotificationFactory,
  private val notificationManager: @SystemService NotificationManager,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val foregroundScopeFactory: () -> Scope<ForegroundScope>
) : Service() {
  private var job: Job? = null

  override fun onCreate() {
    super.onCreate()
    logger.log { "start service" }

    job = scope.launchComposition {
      val states by foregroundManager.states.collectAsState()

      ((if (states.any { it.notification == null })
        remember(states.any { it.notification == null }) {
          listOf(
            inject<ForegroundId>().value to notificationFactory(
              "default_foreground",
              "Foreground",
              NotificationManager.IMPORTANCE_LOW
            ) {
              setContentTitle("${appConfig.appName} is running!")
              setSmallIcon(R.drawable.es_ic_sync)
              setContentIntent(remoteActionOf<StartAppRemoteAction>(context))
            }
          )
        }
      else emptyList()) + states
        .mapNotNull { state ->
          key(state.id) {
            state.notification?.invoke()
              ?.let { state.id to it }
          }
        })
        .forEachIndexed { index, (id, notification) ->
          key(id) {
            DisposableEffect(index, id, notification) {
              logger.log { "update $id" }

              if (index == 0) startForeground(id, notification)
              else notificationManager.notify(id, notification)

              states
                .singleOrNull { it.id == id }
                ?.seen
                ?.complete(Unit)

              onDispose { notificationManager.cancel(id) }
            }
          }
        }

      if (states.isEmpty()) {
        LaunchedEffect(true) {
          onCancel(
            block = {
              logger.log { "stop foreground" }
              stopForeground(true)
              logger.log { "dispatch delayed stop" }
              delay(6.seconds)
              stopSelf()
            },
            onCancel = { logger.log { "cancel delayed stop" } }
          )
        }
      } else {
        val foregroundScope = remember(foregroundScopeFactory)
        DisposableEffect(true) {
          onDispose { foregroundScope.dispose() }
        }
      }
    }
  }

  override fun onDestroy() {
    logger.log { "stop service" }
    job?.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
