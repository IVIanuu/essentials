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

import com.ivianuu.essentials.store.getCurrentData
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.inject
import kotlinx.coroutines.launch

/**
 * Tile which is driven by a boolean pref
 */
abstract class BooleanBoxTileService : BoxTileService<Boolean>() {

    private val dispatchers: AppCoroutineDispatchers by inject()

    override fun onClick() {
        super.onClick()
        scope.launch(dispatchers.computation) {
            val newValue = !box.getCurrentData()
            if (onRequestValueChange(newValue)) {
                box.updateData { newValue }
            }
        }
    }

    protected open suspend fun onRequestValueChange(newValue: Boolean): Boolean = true
}
