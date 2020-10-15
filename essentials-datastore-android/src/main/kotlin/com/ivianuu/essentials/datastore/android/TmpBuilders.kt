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

package com.ivianuu.essentials.datastore.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.map
import kotlin.time.Duration

// todo remove once moshi supports inline classes

fun DiskDataStoreFactory.color(
    name: String,
    produceDefaultData: () -> Color
): DataStore<Color> {
    return create(name = name, produceDefaultData = { produceDefaultData().toArgb() })
        .map(
            fromRaw = { Color(it) },
            toRaw = { it.toArgb() }
        )
}

fun DiskDataStoreFactory.duration(
    name: String,
    produceDefaultData: () -> Duration
): DataStore<Duration> {
    return create(name = name, produceDefaultData = { produceDefaultData().toDouble() })
        .map(
            fromRaw = { it.toDuration() },
            toRaw = { it.toDouble() }
        )
}

private fun Double.toDuration(): Duration {
    return Duration::class.java.getDeclaredConstructor(Double::class.java)
        .also { it.isAccessible = true }
        .newInstance(this)
}

private fun Duration.toDouble(): Double {
    return javaClass.declaredFields
        .first { it.type == Double::class.java }
        .also { it.isAccessible = true }
        .get(this)!! as Double
}
