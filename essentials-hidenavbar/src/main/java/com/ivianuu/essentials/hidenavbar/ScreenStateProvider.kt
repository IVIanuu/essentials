/*
 * Copyright 2018 Manuel Wrage
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

import android.os.PowerManager
import com.ivianuu.injekt.Inject

/**
 * Provides the current screen state
 */
@Inject
internal class ScreenStateProvider(private val powerManager: PowerManager) {
    val isScreenOn: Boolean get() = powerManager.isInteractive
    val isScreenOff: Boolean get() = !isScreenOn
}