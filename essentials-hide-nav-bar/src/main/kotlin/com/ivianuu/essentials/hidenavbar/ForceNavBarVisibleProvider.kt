/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

@JvmInline value class ForceNavBarVisibleProvider(val forceNavBarVisible: Flow<Boolean>)

/**
 * We always wanna show the nav bar on system shut down
 */
context(BroadcastsFactory) @Provide
fun systemShutdownForceNavBarVisibleProvider() = ForceNavBarVisibleProvider(
  broadcasts(Intent.ACTION_SHUTDOWN)
    .take(1)
    .map { true }
    .onStart { emit(false) }
)

@JvmInline value class CombinedForceNavBarVisibleProvider(val forceNavBarVisible: Flow<Boolean>)

context(List<ForceNavBarVisibleProvider>) @Provide fun combinedForceNavBarVisibleProvider() =
  CombinedForceNavBarVisibleProvider(
    combine(map { it.forceNavBarVisible })
      .map { it.any { it } }
  )
