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
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.MoshiSerializerFactory
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.transient
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import java.io.File

@Module
private fun esDataModule() {
    installIn<ApplicationComponent>()
    transient<@DataDir String> { context: @ForApplication Context ->
        context.applicationInfo.dataDir
    }
    transient<@PrefsDir String> { dataDir: @DataDir String -> "$dataDir/prefs" }
    scoped { moshi: Moshi -> MoshiSerializerFactory(moshi) }
    scoped { scope: @ForApplication CoroutineScope,
             dispatchers: AppCoroutineDispatchers,
             prefsDir: @PrefsDir String,
             serializerFactory: MoshiSerializerFactory ->
        DiskDataStoreFactory(
            scope = scope + dispatchers.io,
            produceBoxDirectory = { File(prefsDir) },
            serializerFactory = serializerFactory
        )
    }
    scoped { context: @ForApplication Context,
             scope: @ForApplication CoroutineScope,
             dispatchers: AppCoroutineDispatchers ->
        SettingsDataStoreFactory(
            context = context,
            scope = scope + dispatchers.io
        )
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class DataDir

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class PrefsDir
