/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  companion object {
    @Provide val prefModule = PrefModule("feedback_prefs") { RatePrefs() }
  }
}
