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
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

@Module
private fun esDataModule() {
    installIn<ApplicationComponent>()
    scoped<@PrefsPath String> { context: @ForApplication Context ->
        "${context.applicationInfo.dataDir}/prefs"
    }
    scoped { scope: @ForApplication CoroutineScope,
             dispatchers: AppCoroutineDispatchers,
             prefsPath: @PrefsPath String,
             moshi: Moshi ->
        PrefBoxFactory(
            scope = scope + dispatchers.io,
            prefsPath = prefsPath,
            moshi = moshi
        )
    }
    scoped { context: @ForApplication Context,
             coroutineScope: @ForApplication CoroutineScope,
             dispatchers: AppCoroutineDispatchers ->
        SettingsBoxFactory(
            context = context,
            scope = coroutineScope + dispatchers.io
        )
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class PrefsPath
