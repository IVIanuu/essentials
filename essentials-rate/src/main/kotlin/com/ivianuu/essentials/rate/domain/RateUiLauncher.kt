package com.ivianuu.essentials.rate.domain

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.rate.ui.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.*
import kotlin.time.*

@Provide fun rateUiLauncher(
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  timestampProvider: TimestampProvider,
  _: Logger,
  _: RateUiSchedule = RateUiSchedule()
): ScopeWorker<UiScope> = {
  if (pref.data.first().installTime == 0L) {
    val now = timestampProvider().toLongMilliseconds()
    pref.updateData { copy(installTime = now) }
  }

  pref.updateData { copy(launchTimes = launchTimes.inc()) }

  if (shouldShowRateDialog())
    navigator.push(RateKey)
}

private suspend fun shouldShowRateDialog(
  @Inject pref: DataStore<RatePrefs>,
  @Inject schedule: RateUiSchedule,
  @Inject timestampProvider: TimestampProvider,
  @Inject _: Logger
): Boolean {
  val prefs = pref.data.first()

  if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
    return false.also { d { "show not: already completed" } }

  if (prefs.launchTimes < schedule.minLaunchTimes)
    return false.also {
      d { "show not: launch times -> ${prefs.launchTimes} < ${schedule.minLaunchTimes}" }
    }

  val now = timestampProvider()
  val installedDuration = now - prefs.installTime.toDuration(TimeUnit.MILLISECONDS)
  if (installedDuration <= schedule.minInstallDuration)
    return false.also {
      d { "show not: install duration -> $installedDuration < ${schedule.minInstallDuration}" }
    }

  return true.also { d { "show" } }
}

data class RateUiSchedule(
  val minInstallDuration: Duration = 7.days,
  val minLaunchTimes: Int = 10
)
