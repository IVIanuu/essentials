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

package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.essentials.store.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.prefs.SharedPreferencesImporter
import com.ivianuu.essentials.store.settings.SettingsBoxFactory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Name
import com.ivianuu.injekt.android.ForApplication
import com.ivianuu.injekt.get

@Name
annotation class PrefsPath {
    companion object
}

val EsStoreModule = Module {
    single {
        PrefBoxFactory(
            context = get(name = ForApplication),
            dispatcher = get<AppDispatchers>().io,
            prefsPath = get(name = PrefsPath)
        )
    }
    single(name = PrefsPath) { "${get<Context>().applicationInfo.dataDir}/prefs" }

    single {
        SettingsBoxFactory(
            context = get(name = ForApplication),
            dispatcher = get<AppDispatchers>().io
        )
    }

    factory {
        SharedPreferencesImporter(
            boxFactory = get(),
            context = get(name = ForApplication),
            packageName = get<BuildInfo>().packageName,
            dispatcher = get<AppDispatchers>().io
        )
    }
}
