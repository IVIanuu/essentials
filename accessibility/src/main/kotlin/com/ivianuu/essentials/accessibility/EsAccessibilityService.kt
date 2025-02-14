/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.*
import android.content.*
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMinByOrNull
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide @AndroidComponent class EsAccessibilityService(
  private val accessibilityScopeFactory: (@Service<AccessibilityScope> EsAccessibilityService) -> Scope<AccessibilityScope>,
  private val logger: Logger
) : AccessibilityService() {
  private val _events = EventFlow<AccessibilityEvent>()
  val events: Flow<AccessibilityEvent> by this::_events

  private var accessibilityScope: Scope<AccessibilityScope>? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    logger.d { "accessibility service connected" }
    accessibilityScope = accessibilityScopeFactory(this)
    val configs = accessibilityScope!!.service<Component>().configs
    logger.d { "update accessibility configs $configs" }
    serviceInfo = serviceInfo.apply {
      eventTypes = configs
        .fastMap { it.eventTypes }
        .fastFold(0) { acc, events -> acc.addFlag(events) }

      flags = configs
        .fastMap { it.flags }
        .fastFold(0) { acc, flags -> acc.addFlag(flags) }

      // first one wins
      configs.firstOrNull()?.feedbackType?.let { feedbackType = it }
      feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

      notificationTimeout = configs
        .fastMinByOrNull { it.notificationTimeout }
        ?.notificationTimeout ?: 0L

      packageNames = null
    }
  }

  override fun onAccessibilityEvent(event: AndroidAccessibilityEvent) {
    logger.d { "on accessibility event $event" }
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
    logger.d { "accessibility service disconnected" }
    accessibilityScope?.dispose()
    accessibilityScope = null
    return super.onUnbind(intent)
  }

  @Provide @Service<AccessibilityScope>
  data class Component(val configs: List<AccessibilityConfig>)
}
