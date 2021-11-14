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
