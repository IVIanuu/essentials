/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import com.ivianuu.essentials.Clock
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.days
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.milliseconds
import com.ivianuu.essentials.ui.navigation.UserflowBuilder
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
import kotlin.time.Duration

fun interface RateUserflowBuilder : UserflowBuilder

@Provide fun rateUserflowBuilder(
  clock: Clock,
  logger: Logger,
  pref: DataStore<RatePrefs>,
  schedule: RateUiSchedule = RateUiSchedule()
) = RateUserflowBuilder {
  if (pref.data.first().installTime == 0L) {
    val now = clock()
    pref.updateData { copy(installTime = now.inWholeNanoseconds) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  if (shouldShowRateDialog()) listOf(RateScreen)
  else emptyList()
}

private suspend fun shouldShowRateDialog(
  @Inject clock: Clock,
  @Inject logger: Logger,
  @Inject pref: DataStore<RatePrefs>,
  @Inject schedule: RateUiSchedule
): Boolean {
  val prefs = pref.data.first()

  if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
    return false.also { logger.log { "show not: already completed" } }

  if (prefs.feedbackState == RatePrefs.FeedbackState.NEVER)
    return false.also { logger.log { "show not: user selected never" } }

  if (prefs.launchTimes < schedule.minLaunchTimes)
    return false.also {
      logger.log { "show not: launch times -> ${prefs.launchTimes} < ${schedule.minLaunchTimes}" }
    }

  val now = clock()
  val installedDuration = now - prefs.installTime.milliseconds
  if (installedDuration <= schedule.minInstallDuration)
    return false.also {
      logger.log { "show not: install duration -> $installedDuration < ${schedule.minInstallDuration}" }
    }

  return true.also { logger.log { "show" } }
}

data class RateUiSchedule(
  val minInstallDuration: Duration = 14.days,
  val minLaunchTimes: Int = 10
)
