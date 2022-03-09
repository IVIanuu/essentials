/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.domain

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.rate.data.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.navigation.*
import kotlinx.coroutines.flow.*

fun interface RateOnPlayUseCase : suspend () -> Unit

@Provide fun rateOnPlayUseCase(
  buildInfo: BuildInfo,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
) = RateOnPlayUseCase {
  runCatching {
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
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>
) = ShowNeverUseCase {
  pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
  navigator.pop(key)
}

fun interface ShowLaterUseCase : suspend () -> Unit

@Provide fun showLaterUseCase(
  key: Key<*>,
  navigator: Navigator,
  pref: DataStore<RatePrefs>,
  clock: Clock
) = ShowLaterUseCase {
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
