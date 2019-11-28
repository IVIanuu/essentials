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

package com.ivianuu.essentials.sample.tile

import android.graphics.drawable.Icon
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.boolean
import com.ivianuu.essentials.tile.BooleanBoxTileService
import com.ivianuu.essentials.tile.Tile
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.get

class DummyTileService : BooleanBoxTileService() {

    override val box by unsafeLazy {
        get<PrefBoxFactory>().boolean("tile_state")
    }

    override fun createTile(state: Boolean): Tile = Tile(
        Icon.createWithResource(this, R.drawable.es_ic_link),
        if (state) "Enabled" else "Disabled"
    )

}