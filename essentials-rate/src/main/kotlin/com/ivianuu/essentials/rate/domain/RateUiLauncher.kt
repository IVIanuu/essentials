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

package com.ivianuu.essentials.rate.domain

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.rate.data.RatePrefs
import com.ivianuu.essentials.rate.ui.RateKey
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.days
import kotlin.time.toDuration

@Provide fun rateUiLauncher(
  logger: Logger,
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  schedule: RateUiSchedule = RateUiSchedule(),
  clock: Clock
): ScopeWorker<UiComponent> = {
  if (pref.data.first().installTime == 0L) {
    val now = clock()
    pref.updateData { copy(installTime = now.inWholeNanoseconds) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  if (shouldShowRateDialog())
    navigator.push(RateKey)
}

private suspend fun shouldShowRateDialog(
  @Inject logger: Logger,
  @Inject pref: DataStore<RatePrefs>,
  @Inject schedule: RateUiSchedule,
  @Inject clock: Clock
): Boolean {
  val prefs = pref.data.first()

  if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
    return false.also { log { "show not: already completed" } }

  if (prefs.feedbackState == RatePrefs.FeedbackState.NEVER)
    return false.also { log { "show not: user selected never" } }

  if (prefs.launchTimes < schedule.minLaunchTimes)
    return false.also {
      log { "show not: launch times -> ${prefs.launchTimes} < ${schedule.minLaunchTimes}" }
    }

  val now = clock()
  val installedDuration = now - prefs.installTime.toDuration(TimeUnit.NANOSECONDS)
  if (installedDuration <= schedule.minInstallDuration)
    return false.also {
      log { "show not: install duration -> $installedDuration < ${schedule.minInstallDuration}" }
    }

  return true.also { log { "show" } }
}

data class RateUiSchedule(
  val minInstallDuration: Duration = 14.days,
  val minLaunchTimes: Int = 10
)
