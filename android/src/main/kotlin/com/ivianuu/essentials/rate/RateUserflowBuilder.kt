/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.time.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

fun interface RateUserflowBuilder : UserflowBuilder

@Provide fun rateUserflowBuilder(
  @Inject clock: Clock,
  @Inject logger: Logger,
  @Inject pref: DataStore<RatePrefs>,
  @Inject schedule: RateUiSchedule
) = RateUserflowBuilder {
  if (pref.data.first().installTime == 0L) {
    val now = clock()
    pref.updateData { copy(installTime = now.inWholeNanoseconds) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  suspend fun shouldShowRateDialog(): Boolean {
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

  if (shouldShowRateDialog()) listOf(RateScreen())
  else emptyList()
}

data class RateUiSchedule(
  val minInstallDuration: Duration = 14.days,
  val minLaunchTimes: Int = 10
) {
  @Provide companion object {
    @Provide val default get() = RateUiSchedule(Duration.INFINITE, Int.MIN_VALUE)
  }
}
