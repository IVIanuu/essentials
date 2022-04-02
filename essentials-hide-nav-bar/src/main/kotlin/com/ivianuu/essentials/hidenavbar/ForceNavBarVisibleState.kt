/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

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
