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

package com.ivianuu.essentials.tile

import android.service.quicksettings.TileService
import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.injekt.android.ServiceComponent
import com.ivianuu.injekt.android.createServiceComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Base tile service
 */
abstract class EsTileService : TileService() {

    val serviceComponent by lazy { createServiceComponent() }

    private val defaultDispatcher: DefaultDispatcher by lazy {
        serviceComponent.mergeComponent<EsTileServiceComponent>()
            .defaultDispatcher
    }

    val scope by lazy { CoroutineScope(defaultDispatcher) }

    lateinit var listeningCoroutineScope: CoroutineScope
        private set

    override fun onStartListening() {
        super.onStartListening()
        listeningCoroutineScope = CoroutineScope(defaultDispatcher)
    }

    override fun onStopListening() {
        listeningCoroutineScope.cancel()
        super.onStopListening()
    }

    override fun onDestroy() {
        listeningCoroutineScope.cancel()
        super.onDestroy()
    }
}

@MergeInto(ServiceComponent::class)
interface EsTileServiceComponent {
    val defaultDispatcher: DefaultDispatcher
}
