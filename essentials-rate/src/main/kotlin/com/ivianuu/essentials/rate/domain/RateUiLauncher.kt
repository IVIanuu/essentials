/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.domain

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.rate.ui.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.time.days
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration

@Provide fun rateUiLauncher(
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  clock: Clock,
  L: Logger,
  S: RateUiSchedule = RateUiSchedule()
) = ScopeWorker<UiScope> {
  if (pref.data.first().installTime == 0L) {
    val now = clock()
    pref.updateData { copy(installTime = now.inWholeNanoseconds) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  if (shouldShowRateDialog())
    navigator.push(RateKey)
}

private suspend fun shouldShowRateDialog(
  @Inject pref: DataStore<RatePrefs>,
  schedule: RateUiSchedule,
  clock: Clock,
  L: Logger
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
  val installedDuration = now - prefs.installTime.milliseconds
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
