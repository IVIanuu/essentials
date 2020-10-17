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

package com.ivianuu.essentials.screenstate

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

@FunBinding
fun configChanges(
    applicationContext: ApplicationContext,
    mainDispatcher: MainDispatcher,
): Flow<Unit> = callbackFlow<Unit> {
    val callbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            offerSafe(Unit)
        }

        override fun onLowMemory() {
        }

        override fun onTrimMemory(level: Int) {
        }
    }
    applicationContext.registerComponentCallbacks(callbacks)
    awaitClose { applicationContext.unregisterComponentCallbacks(callbacks) }
}.flowOn(mainDispatcher)
