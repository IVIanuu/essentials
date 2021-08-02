/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
