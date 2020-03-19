/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.tile

import android.service.quicksettings.TileService
import com.ivianuu.essentials.app.AppComponentHolder
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.ComponentBuilderInterceptor
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.ComponentOwner
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * Base tile service
 */
abstract class EsTileService : TileService(), ComponentOwner,
    ComponentBuilderInterceptor {

    override val component by unsafeLazy {
        ServiceComponent(this) {
            buildComponent()
        }
    }

    val coroutineScope by unsafeLazy {
        CoroutineScope(
            Job() + AppComponentHolder.get<AppCoroutineDispatchers>().computation
        )
    }

    lateinit var listeningCoroutineScope: CoroutineScope
        private set

    override fun onDestroy() {
        listeningCoroutineScope.cancel()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        listeningCoroutineScope = CoroutineScope(
            Job() +
                    AppComponentHolder.get<AppCoroutineDispatchers>().computation
        )
    }

    override fun onStopListening() {
        listeningCoroutineScope.cancel()
        super.onStopListening()
    }
}
