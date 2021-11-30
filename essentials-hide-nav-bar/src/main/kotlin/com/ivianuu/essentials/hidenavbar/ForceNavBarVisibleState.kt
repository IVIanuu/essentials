/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar

import android.content.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

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
