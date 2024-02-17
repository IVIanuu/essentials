/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate

import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.time.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide class RateUseCases(
  private val appConfig: AppConfig,
  private val clock: Clock,
  private val screen: Screen<*>,
  private val navigator: Navigator,
  private val pref: DataStore<RatePrefs>
) {
  suspend fun rateOnPlay() {
    catch {
      navigator.push(PlayStoreAppDetailsKey(appConfig.packageName))
      pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.COMPLETED) }
    }.printErrors()
  }

  suspend fun shouldDisplayShowNever(): Boolean =
    pref.data.first().feedbackState == RatePrefs.FeedbackState.LATER

  suspend fun showNever() {
    pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
    navigator.pop(screen)
  }

  suspend fun showLater() {
    val now = clock()
    pref.updateData {
      copy(
        launchTimes = 0,
        installTime = now.inWholeMilliseconds,
        feedbackState = RatePrefs.FeedbackState.LATER
      )
    }
    navigator.pop(screen)
  }
}
