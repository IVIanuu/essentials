/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.data

import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class RatePrefs(
  @SerialName("launch_times") val launchTimes: Int = 0,
  @SerialName("install_time") val installTime: Long = 0L,
  @SerialName("state") val feedbackState: FeedbackState? = null
) {
  enum class FeedbackState {
    COMPLETED,
    LATER,
    NEVER
  }

  companion object {
    @Provide val prefModule = PrefModule { RatePrefs() }
  }
}
