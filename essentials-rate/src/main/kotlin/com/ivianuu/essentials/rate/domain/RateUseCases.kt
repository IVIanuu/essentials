/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.rate.domain

import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.rate.data.RatePrefs
import com.ivianuu.essentials.time.Clock
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PlayStoreAppDetailsKey
import com.ivianuu.essentials.ui.navigation.pop
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

interface RateUseCases {
  suspend fun rateOnPlay()

  suspend fun shouldDisplayShowNever(): Boolean

  suspend fun showNever()

  suspend fun showLater()
}

@Provide class RateUsecasesImpl(
  private val buildInfo: BuildInfo,
  private val clock: Clock,
  private val key: Key<*>,
  private val navigator: Navigator,
  private val pref: DataStore<RatePrefs>
) : RateUseCases {
  override suspend fun rateOnPlay() {
    catch {
      navigator.push(PlayStoreAppDetailsKey(buildInfo.packageName))
      pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.COMPLETED) }
    }.onFailure { it.printStackTrace() }
  }

  override suspend fun shouldDisplayShowNever(): Boolean =
    pref.data.first().feedbackState == RatePrefs.FeedbackState.LATER

  override suspend fun showNever() {
    pref.updateData { copy(feedbackState = RatePrefs.FeedbackState.NEVER) }
    navigator.pop(key)
  }

  override suspend fun showLater() {
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
}
