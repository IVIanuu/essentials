package com.ivianuu.essentials.rate.domain

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

typealias RateOnPlayUseCase = suspend () -> Unit

@Provide fun rateOnPlayUseCase(
  buildInfo: BuildInfo,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
): RateOnPlayUseCase = {
  catch {
    navigator.push(
      UrlKey("https://play.google.com/store/apps/details?id=${buildInfo.packageName}")
    )
    pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.COMPLETED) }
  }.onFailure { it.printStackTrace() }
}

internal typealias DisplayShowNeverUseCase = suspend () -> Boolean

@Provide fun displayShowNeverUseCase(pref: DataStore<RatePrefs>): DisplayShowNeverUseCase = {
  pref.data.first().feedbackState == RatePrefs.FeedbackState.LATER
}

internal typealias ShowNeverUseCase = suspend () -> Unit

@Provide fun showNeverUseCase(
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
): ShowNeverUseCase = {
  pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
  navigator.pop(key)
}

internal typealias ShowLaterUseCase = suspend () -> Unit

@Provide fun showLaterUseCase(
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  timestampProvider: TimestampProvider
): ShowLaterUseCase = {
  val now = timestampProvider().toLongMilliseconds()
  pref.updateData {
    copy(
      launchTimes = 0,
      installTime = now,
      feedbackState = RatePrefs.FeedbackState.LATER
    )
  }
  navigator.pop(key)
}
