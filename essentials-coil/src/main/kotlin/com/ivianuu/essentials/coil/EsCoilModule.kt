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

package com.ivianuu.essentials.coil

import android.content.Context
import coil.CoilAccessor
import coil.ImageLoader
import coil.decode.Decoder
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.set

@Module
fun esCoilModule() {
    installIn<ApplicationComponent>()
    set<Decoder>()
    set<FetcherBinding<*>>()
    set<MapperBinding<*>>()
    set<MeasuredMapperBinding<*>>()

    scoped {
        ImageLoader.Builder(get<@ForApplication Context>())
            .componentRegistry {
                get<Set<Decoder>>().forEach { add(it) }
                get<Set<FetcherBinding<*>>>()
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.fetcher)
                    }
                get<Set<MapperBinding<*>>>()
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }
                get<Set<MeasuredMapperBinding<*>>>()
                    .forEach { binding ->
                        CoilAccessor.add(this, binding.type.java, binding.mapper)
                    }
            }
            .build()
    }
}
