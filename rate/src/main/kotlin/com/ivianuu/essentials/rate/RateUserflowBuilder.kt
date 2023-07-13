/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.ui.navigation.UserflowBuilder
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

fun interface RateUserflowBuilder : UserflowBuilder

context(Clock, Logger) @Provide fun rateUserflowBuilder(
  pref: DataStore<RatePrefs>,
  schedule: RateUiSchedule
) = RateUserflowBuilder {
  if (pref.data.first().installTime == 0L) {
    val now = now()
    pref.updateData { copy(installTime = now.inWholeNanoseconds) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  if (shouldShowRateDialog(pref, schedule)) listOf(RateScreen)
  else emptyList()
}

context(Clock, Logger) private suspend fun shouldShowRateDialog(
  pref: DataStore<RatePrefs>,
  schedule: RateUiSchedule
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

  val now = now()
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
