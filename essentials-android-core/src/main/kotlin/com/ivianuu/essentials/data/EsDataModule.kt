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
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.unscoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import java.io.File

@Module
fun EsDataModule() {
    installIn<ApplicationComponent>()
    unscoped<@DataDir String> {
        get<@ForApplication Context>().applicationInfo.dataDir
    }
    unscoped<@PrefsDir String> { "${get<@DataDir String>()}/prefs" }
    scoped { MoshiSerializerFactory(get()) }
    scoped {
        DiskDataStoreFactory(
            scope = globalScope + dispatchers.io,
            produceBoxDirectory = { File(get<@PrefsDir String>()) },
            serializerFactory = get()
        )
    }
    scoped {
        SettingsDataStoreFactory(
            context = get<@ForApplication Context>(),
            scope = get<@GlobalScope CoroutineScope>() + get<AppCoroutineDispatchers>().io
        )
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class DataDir

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class PrefsDir
