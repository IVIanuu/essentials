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

@Given fun rateUiLauncher(
  @Given navigator: Navigator,
  @Given pref: DataStore<RatePrefs>,
  @Given shouldShowRateDialog: ShouldShowRateDialogUseCase,
  @Given timestampProvider: TimestampProvider
): ScopeWorker<UiGivenScope> = {
  if (pref.data.first().installTime == 0L) {
    pref.updateData { copy(installTime = timestampProvider().toLongMilliseconds()) }
  }
  pref.updateData { copy(launchTimes = launchTimes.inc()) }
  if (shouldShowRateDialog()) {
    navigator.push(RateKey)
  }
}

internal typealias ShouldShowRateDialogUseCase = suspend () -> Boolean

@Given fun shouldShowRateDialogUseCase(
  @Given logger: Logger,
  @Given pref: DataStore<RatePrefs>,
  @Given schedule: RateUiSchedule = RateUiSchedule(),
  @Given timestampProvider: TimestampProvider
): ShouldShowRateDialogUseCase = useCase@{
  val prefs = pref.data.first()
  if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
    return@useCase false.also { d { "show not: already completed" } }
  if (prefs.launchTimes < schedule.minLaunchTimes)
    return@useCase false.also {
      d { "show not: launch times -> ${prefs.launchTimes} < ${schedule.minLaunchTimes}" }
    }
  val now = timestampProvider()
  val installedDuration = now - prefs.installTime.toDuration(TimeUnit.MILLISECONDS)
  if (installedDuration <= schedule.minInstallDuration)
    return@useCase false.also {
      d { "show not: install duration -> $installedDuration < ${schedule.minInstallDuration}" }
    }

  return@useCase true
    .also { d { "show" } }
}

data class RateUiSchedule(
  val minInstallDuration: Duration = 7.days,
  val minLaunchTimes: Int = 10
)
