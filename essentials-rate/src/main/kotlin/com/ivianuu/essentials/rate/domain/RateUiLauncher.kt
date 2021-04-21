package com.ivianuu.essentials.rate.domain

import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.rate.ui.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.*
import kotlin.time.*

@Given
fun rateUiLauncher(
    @Given navigator: Navigator,
    @Given pref: DataStore<RatePrefs>,
    @Given shouldShowRateDialog: ShouldShowRateDialogUseCase
): ScopeWorker<UiGivenScope> = {
    pref.updateData { copy(launchTimes = launchTimes.inc()) }
    if (shouldShowRateDialog()) {
        navigator.push(RateKey)
    }
}

internal typealias ShouldShowRateDialogUseCase = suspend () -> Boolean

@Given
fun shouldShowRateDialogUseCase(
    @Given pref: DataStore<RatePrefs>,
    @Given timestampProvider: TimestampProvider
): ShouldShowRateDialogUseCase = useCase@ {
    val prefs = pref.data.first()
    if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
        return@useCase false
    if (prefs.launchTimes < MIN_LAUNCH_TIMES)
        return@useCase false
    val now = timestampProvider()
    val installedDuration = now - prefs.installTime.toDuration(TimeUnit.MILLISECONDS)
    return@useCase installedDuration >= MIN_INSTALL_DURATION
}

private val MIN_INSTALL_DURATION = 3.days
private const val MIN_LAUNCH_TIMES = 10
