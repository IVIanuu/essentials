/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.android

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.LoggingTag
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

@Provide class AndroidAnalytics(
  private val buildInfo: BuildInfo,
  private val firebaseAnalytics: () -> FirebaseAnalytics,
  private val L: Logger
) : Analytics {
  override fun log(name: String, params: Map<String, String>) {
    if (buildInfo.isDebug) {
      log(tag = LoggingTag("Analytics")) { "$name: $params" }
    } else {
      firebaseAnalytics().logEvent(name) {
        params.forEach { param(it.key, it.value) }
      }
    }
  }
}

object FirebaseModule {
  @Provide val firebaseAnalytics: FirebaseAnalytics
    get() = Firebase.analytics
}
