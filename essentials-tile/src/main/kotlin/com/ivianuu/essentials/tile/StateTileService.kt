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

import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.composition.runReader
import com.ivianuu.injekt.get

/**
 * Stateful tile service
 */
abstract class StateTileService<T> : EsTileService() {

    abstract fun createTile(state: T): Tile

    protected fun setState(state: T) {
        setTile(createTile(state))
    }

    fun setTile(tile: Tile) {
        component.runReader {
            val qsTile = qsTile ?: return

            qsTile.state = when (tile.state) {
                Tile.State.Active -> android.service.quicksettings.Tile.STATE_ACTIVE
                Tile.State.Inactive -> android.service.quicksettings.Tile.STATE_INACTIVE
                Tile.State.Unavailable -> android.service.quicksettings.Tile.STATE_UNAVAILABLE
            }
            qsTile.icon = tile.icon
            qsTile.label = when {
                tile.label != null -> tile.label
                tile.labelRes != null -> Resources.getString(tile.labelRes)
                else -> null
            }
            qsTile.contentDescription = when {
                tile.description != null -> tile.description
                tile.descriptionRes != null -> Resources.getString(tile.descriptionRes)
                else -> null
            }
            qsTile.updateTile()
        }
    }
}
