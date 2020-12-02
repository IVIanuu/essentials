/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.foreground

import android.content.Intent
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.app.AppWorkerBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlin.time.milliseconds

@AppWorkerBinding
@FunBinding
suspend fun applyAndroidForegroundServiceState(
    applicationContext: ApplicationContext,
    logger: Logger,
    state: Flow<InternalForegroundState>,
) {
    state
        .debounce(300.milliseconds)
        .filter { it.isForeground }
        .onEach {
            logger.d { "start foreground service $it" }
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, ForegroundService::class.java)
            )
        }
        .collect()
}
