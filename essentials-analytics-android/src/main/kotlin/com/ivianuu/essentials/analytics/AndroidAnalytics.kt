/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.LoggingTag
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

@Provide class AndroidFirebaseAnalytics(private val firebaseAnalytics: FirebaseAnalytics) : Analytics {
  override fun log(name: String, params: Map<String, String>) {
    firebaseAnalytics.logEvent(name) {
      params
        .forEach { param(it.key, it.value) }
    }
  }

  override fun setUserProperty(name: String, value: String) {
    firebaseAnalytics.setUserProperty(name, value)
  }
}

@Provide class AndroidLoggingAnalytics(private val L: Logger) : Analytics {
  private val properties = mutableMapOf<String, String>()

  override fun log(name: String, params: Map<String, String>) {
    log(tag = LoggingTag("Analytics")) { "$name: $params" }
  }

  override fun setUserProperty(name: String, value: String) {
    synchronized(properties) {
      properties[name] = value
      properties.toMap()
    }.let {
      log(tag = LoggingTag("Analytics")) { "properties changed $it" }
    }
  }
}

object AnalyticsModule {
  @Provide inline fun androidAnalytics(
    buildInfo: BuildInfo,
    firebaseAnalytics: () -> AndroidFirebaseAnalytics,
    loggingAnalytics: () -> AndroidLoggingAnalytics
  ): Analytics = if (buildInfo.isDebug) loggingAnalytics() else firebaseAnalytics()

  @Provide val firebaseAnalytics: FirebaseAnalytics
    get() = Firebase.analytics
}
