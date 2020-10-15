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

import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.MoshiSerializerFactory
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.IODispatcher
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationContext
import com.ivianuu.injekt.merge.ApplicationComponent
import com.squareup.moshi.Moshi
import kotlinx.coroutines.plus
import java.io.File

typealias DataDir = File

@Binding
fun dataDir(applicationContext: ApplicationContext): DataDir =
    File(applicationContext.applicationInfo.dataDir)

typealias PrefsDir = File

@Binding
fun prefsDir(dataDir: DataDir): PrefsDir = dataDir.resolve("prefs")

@Binding(ApplicationComponent::class)
fun moshiSerializerFactory(moshi: Moshi) = MoshiSerializerFactory(moshi)

@Binding(ApplicationComponent::class)
fun diskDataStoreFactory(
    globalScope: GlobalScope,
    ioDispatcher: IODispatcher,
    prefsDir: () -> PrefsDir,
    serializerFactory: MoshiSerializerFactory,
) = DiskDataStoreFactory(
    scope = globalScope + ioDispatcher,
    produceBoxDirectory = prefsDir,
    serializerFactory = serializerFactory
)

@Binding(ApplicationComponent::class)
fun settingsDataStoreFactory(
    applicationContext: ApplicationContext,
    ioDispatcher: IODispatcher,
    globalScope: GlobalScope,
) = SettingsDataStoreFactory(
    context = applicationContext,
    scope = globalScope + ioDispatcher
)

@Binding
fun packageManager(applicationContext: ApplicationContext) = applicationContext.packageManager!!
