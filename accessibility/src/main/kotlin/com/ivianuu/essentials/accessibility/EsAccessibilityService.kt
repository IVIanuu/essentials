/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.*
import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide @AndroidComponent class EsAccessibilityService(
  private val accessibilityScopeFactory: (@Service<AccessibilityScope> EsAccessibilityService) -> Scope<AccessibilityScope>,
  private val logger: Logger
) : AndroidAccessibilityService() {
  private val _events = EventFlow<AccessibilityEvent>()
  val events: Flow<AccessibilityEvent> by this::_events

  private var accessibilityScope: Scope<AccessibilityScope>? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    logger.log { "service connected" }
    accessibilityScope = accessibilityScopeFactory(this)
    val accessibilityComponent = accessibilityScope!!
      .service<AccessibilityComponent>()

    val configs = accessibilityComponent.configs
    logger.log { "update config from $configs" }
    serviceInfo = serviceInfo?.apply {
      eventTypes = configs
        .map { it.eventTypes }
        .fold(0) { acc, events -> acc.addFlag(events) }

      flags = configs
        .map { it.flags }
        .fold(0) { acc, flags -> acc.addFlag(flags) }

      // first one wins
      configs.firstOrNull()?.feedbackType?.let { feedbackType = it }
      feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

      notificationTimeout = configs
        .map { it.notificationTimeout }
        .minOrNull() ?: 0L

      packageNames = null
    }
  }

  override fun onAccessibilityEvent(event: AndroidAccessibilityEvent) {
    logger.log { "on accessibility event $event" }
    _events.tryEmit(
      AccessibilityEvent(
        type = event.eventType,
        packageName = event.packageName?.toString(),
        className = event.className?.toString(),
        isFullScreen = event.isFullScreen
      )
    )
  }

  override fun onInterrupt() {
  }

  override fun onUnbind(intent: Intent?): Boolean {
    logger.log { "service disconnected" }
    accessibilityScope?.dispose()
    accessibilityScope = null
    return super.onUnbind(intent)
  }
}

@Provide @Service<AccessibilityScope> data class AccessibilityComponent(
  val configs: List<AccessibilityConfig>,
  val coroutineScope: ScopedCoroutineScope<AccessibilityScope>
)
