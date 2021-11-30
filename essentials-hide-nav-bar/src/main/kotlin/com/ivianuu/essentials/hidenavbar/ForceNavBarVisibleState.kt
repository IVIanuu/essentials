/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.Intent
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.state.produceValue
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

@JvmInline value class ForceNavBarVisibleState(val value: Boolean)

/**
 * We always wanna show the nav bar on system shut down
 */
@Provide fun systemShutdownForceNavBarVisibleState(
  broadcastsFactory: BroadcastsFactory
): @Composable () -> ForceNavBarVisibleState = {
  produceValue(ForceNavBarVisibleState(false)) {
    broadcastsFactory(Intent.ACTION_SHUTDOWN).first()
    ForceNavBarVisibleState(true)
  }
}

@JvmInline value class CombinedForceNavBarVisibleState(val value: Boolean)

@Provide fun combinedForceNavBarVisibleState(
  forceNavbarVisibleStates: List<@Composable () -> ForceNavBarVisibleState>
): @Composable () -> CombinedForceNavBarVisibleState = {
  CombinedForceNavBarVisibleState(
    forceNavbarVisibleStates
      .any { it().value }
  )
}
