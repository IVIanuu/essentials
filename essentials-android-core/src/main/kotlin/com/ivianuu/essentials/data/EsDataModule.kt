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

package com.ivianuu.essentials.data

import android.content.Context
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.android.settings.SettingsBoxFactory
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

@ApplicationScope
@Module
private fun ComponentBuilder.esDataModule() {
    single(qualifier = PrefsPath) { "${get<Context>().applicationInfo.dataDir}/prefs" }

    single {
        PrefBoxFactory(
            coroutineScope = get<CoroutineScope>(ForApplication) + get<AppCoroutineDispatchers>().io,
            prefsPath = get(qualifier = PrefsPath),
            moshi = get()
        )
    }

    single {
        SettingsBoxFactory(
            context = get(qualifier = ForApplication),
            coroutineScope = get<CoroutineScope>(ForApplication) + get<AppCoroutineDispatchers>().io
        )
    }
}

@QualifierMarker
annotation class PrefsPath {
    companion object : Qualifier.Element
}
