/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import com.ivianuu.essentials.data.DataStoreModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Serializable data class RatePrefs(
  val launchTimes: Int = 0,
  val installTime: Long = 0L,
  val feedbackState: FeedbackState? = null
) {
  enum class FeedbackState { COMPLETED, LATER, NEVER }

  companion object {
    @Provide val prefModule = DataStoreModule("rate_prefs") { RatePrefs() }
  }
}
