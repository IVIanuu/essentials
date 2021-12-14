/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@JvmInline value class ForceNavBarVisibleState(val value: Boolean)

/**
 * We always wanna show the nav bar on system shut down
 */
@Provide fun systemShutdownForceNavBarVisibleState(
  broadcastsFactory: BroadcastsFactory
): Flow<ForceNavBarVisibleState> = broadcastsFactory(Intent.ACTION_SHUTDOWN)
  .take(1)
  .map { ForceNavBarVisibleState(true) }
  .onStart { emit(ForceNavBarVisibleState(false)) }

@JvmInline value class CombinedForceNavBarVisibleState(val value: Boolean)

@Provide fun combinedForceNavBarVisibleState(
  forceNavbarVisibleStates: List<Flow<ForceNavBarVisibleState>>
): Flow<CombinedForceNavBarVisibleState> = combine(forceNavbarVisibleStates) { states ->
  CombinedForceNavBarVisibleState(states.any { it.value })
}
