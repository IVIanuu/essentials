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

@Given
fun rateUiLauncher(
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

@Given
fun shouldShowRateDialogUseCase(
    @Given logger: Logger,
    @Given pref: DataStore<RatePrefs>,
    @Given timestampProvider: TimestampProvider
): ShouldShowRateDialogUseCase = useCase@ {
    val prefs = pref.data.first()
    if (prefs.feedbackState == RatePrefs.FeedbackState.COMPLETED)
        return@useCase false.also {
            logger.d { "show not: already completed" }
        }
    if (prefs.launchTimes < MIN_LAUNCH_TIMES)
        return@useCase false.also {
            logger.d { "show not: launch times -> ${prefs.launchTimes} < $MIN_LAUNCH_TIMES" }
        }
    val now = timestampProvider()
    val installedDuration = now - prefs.installTime.toDuration(TimeUnit.MILLISECONDS)
    if (installedDuration <= MIN_INSTALL_DURATION)
        return@useCase false.also {
            logger.d { "show not: install duration -> $installedDuration < $MIN_INSTALL_DURATION" }
        }

    return@useCase true
        .also { logger.d { "show" } }
}

private val MIN_INSTALL_DURATION = 10.seconds//7.days
private const val MIN_LAUNCH_TIMES = 10
