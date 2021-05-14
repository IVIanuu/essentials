package com.ivianuu.essentials.rate.data

import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.injekt.*
import kotlinx.serialization.*

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
}

@Given val ratePrefsModule = PrefModule("feedback_prefs") { RatePrefs() }
