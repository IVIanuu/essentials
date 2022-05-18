/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.domain

import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.analytics.Analytics
import com.ivianuu.essentials.analytics.log
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.rate.data.RatePrefs
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PlayStoreAppDetailsKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

fun interface RateOnPlayUseCase : suspend () -> Unit

@Provide fun rateOnPlayUseCase(
  analytics: Analytics,
  buildInfo: BuildInfo,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
) = RateOnPlayUseCase {
  catch {
    analytics.logRateResult("rate_on_play")
    navigator.push(PlayStoreAppDetailsKey(buildInfo.packageName))
    pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.COMPLETED) }
  }.onFailure { it.printStackTrace() }
}

fun interface DisplayShowNeverUseCase : suspend () -> Boolean

@Provide fun displayShowNeverUseCase(pref: DataStore<RatePrefs>) = DisplayShowNeverUseCase {
  pref.data.first().feedbackState == RatePrefs.FeedbackState.LATER
}

fun interface ShowNeverUseCase : suspend () -> Unit

@Provide fun showNeverUseCase(
  analytics: Analytics,
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
) = ShowNeverUseCase {
  analytics.logRateResult("show_never")
  pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
  navigator.pop(key)
}

fun interface ShowLaterUseCase : suspend () -> Unit

@Provide fun showLaterUseCase(
  analytics: Analytics,
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  clock: Clock
) = ShowLaterUseCase {
  analytics.logRateResult("show_later")

  val now = clock()
  pref.updateData {
    copy(
      launchTimes = 0,
      installTime = now.inWholeMilliseconds,
      feedbackState = RatePrefs.FeedbackState.LATER
    )
  }
  navigator.pop(key)
}

private fun Analytics.logRateResult(result: String) {
  log("rate_shown") {
    put("result", result)
  }
}
