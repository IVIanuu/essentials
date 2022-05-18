/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.LoggingTag
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch

@Provide class AndroidAnalytics(
  private val buildInfo: BuildInfo,
  private val firebaseAnalytics: () -> FirebaseAnalytics,
  private val L: Logger,
  private val scope: NamedCoroutineScope<AppScope>,
  private val analyticsParamsContributors: List<AnalyticsParamsContributor>
) : Analytics {
  override fun log(name: String, params: Map<String, String>) {
    scope.launch {
      val finalParams = params
        .toMutableMap()
        .apply {
          analyticsParamsContributors.forEach {
            it(this, name)
          }
        }
        .toList()
        .sortedBy { it.first }
        .toMap()

      if (buildInfo.isDebug) {
        log(tag = LoggingTag("Analytics")) { "$name: $finalParams" }
      } else {
        firebaseAnalytics().logEvent(name) {
          finalParams
            .forEach { param(it.key, it.value) }
        }
      }
    }
  }
}

object FirebaseModule {
  @Provide val firebaseAnalytics: FirebaseAnalytics
    get() = Firebase.analytics
}
