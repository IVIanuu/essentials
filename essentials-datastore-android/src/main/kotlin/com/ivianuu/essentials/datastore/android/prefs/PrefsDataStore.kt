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

package com.ivianuu.essentials.datastore.android.prefs

import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.Serializer
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent

typealias PrefsDataStore = DataStore<Prefs>
@Binding(ApplicationComponent::class)
fun prefsDataStore(
    factory: DiskDataStoreFactory,
    lazyPrefsSerializer: () -> Serializer<Prefs>
): PrefsDataStore {
    return factory.create(
        name = "prefs",
        produceDefaultData = { mutablePrefsOf() },
        produceSerializer = lazyPrefsSerializer
    )
}

suspend fun PrefsDataStore.edit(block: MutablePrefs.() -> Unit): Prefs {
    return updateData {
        MutablePrefs(it.asMap().toMutableMap())
            .apply(block)
    }
}
