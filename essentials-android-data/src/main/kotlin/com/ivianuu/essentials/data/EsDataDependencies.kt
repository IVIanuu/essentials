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

package com.ivianuu.essentials.data

import JsonSerializerFactory
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.coroutines.IODispatcher
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.datastore.disk.DiskDataStoreFactory
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.plus
import kotlinx.serialization.json.Json
import java.io.File

typealias DataDir = File

@Binding
fun dataDir(applicationContext: ApplicationContext): DataDir =
    File(applicationContext.applicationInfo.dataDir)

typealias PrefsDir = File

@Binding
fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")

@Binding(ApplicationComponent::class)
fun jsonSerializerFactory(json: Json) = JsonSerializerFactory()

@Binding(ApplicationComponent::class)
fun diskDataStoreFactory(
    globalScope: GlobalScope,
    ioDispatcher: IODispatcher,
    prefsDir: () -> PrefsDir,
    lazySerializerFactory: () -> JsonSerializerFactory,
) = DiskDataStoreFactory(
    scope = globalScope + ioDispatcher,
    produceDataStoreDirectory = prefsDir,
    lazySerializerFactory = lazySerializerFactory
)

@Binding(ApplicationComponent::class)
fun settingsDataStoreFactory(
    applicationContext: ApplicationContext,
    ioDispatcher: IODispatcher,
    mainDispatcher: MainDispatcher,
    globalScope: GlobalScope,
) = SettingsDataStoreFactory(
    context = applicationContext,
    mainDispatcher = mainDispatcher,
    scope = globalScope + ioDispatcher
)

@Binding
fun packageManager(applicationContext: ApplicationContext) = applicationContext.packageManager!!
