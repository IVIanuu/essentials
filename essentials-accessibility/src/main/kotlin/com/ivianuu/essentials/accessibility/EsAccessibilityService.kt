/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.addFlag
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EsAccessibilityService : AccessibilityService() {
  private val serviceComponent by lazy {
    application
      .cast<AppElementsOwner>()
      .appElements
      .element<EsAccessibilityServiceComponent>()
  }

  @Provide private val logger get() = serviceComponent.logger

  private var accessibilityComponent: AccessibilityComponent? = null

  override fun onServiceConnected() {
    super.onServiceConnected()
    log { "service connected" }
    serviceComponent.accessibilityServiceRef.accessibilityService
      .cast<MutableStateFlow<EsAccessibilityService?>>().value = this
    val accessibilityComponent = serviceComponent.accessibilityComponentFactory(Scope(), this)
      .also { this.accessibilityComponent = it }

    accessibilityComponent.coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      val configs = accessibilityComponent.configs()
      log { "update config from $configs" }
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
          .maxOrNull() ?: 0L

        packageNames = null
      }
    }
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    log { "on accessibility event $event" }
    serviceComponent.accessibilityEvents.tryEmit(
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
    log { "service disconnected" }
    accessibilityComponent?.scope?.dispose()
    accessibilityComponent = null
    serviceComponent.accessibilityServiceRef.accessibilityService
      .cast<MutableStateFlow<EsAccessibilityService?>>().value = null
    return super.onUnbind(intent)
  }
}

@JvmInline value class AccessibilityServiceProvider(val accessibilityService: Flow<EsAccessibilityService?>) {
  companion object {
    @Provide val default = AccessibilityServiceProvider(MutableStateFlow(null))
  }
}

@Provide @Element<AppScope>
data class EsAccessibilityServiceComponent(
  val accessibilityEvents: MutableSharedFlow<com.ivianuu.essentials.accessibility.AccessibilityEvent>,
  val accessibilityComponentFactory: (Scope<AccessibilityScope>, EsAccessibilityService) -> AccessibilityComponent,
  val logger: Logger,
  val accessibilityServiceRef: AccessibilityServiceProvider
)

@Provide data class AccessibilityComponent(
  val configs: () -> List<AccessibilityConfig>,
  val coroutineScope: NamedCoroutineScope<AccessibilityScope>,
  val elements: Elements<AccessibilityScope>,
  val scope: Scope<AccessibilityScope>
)
