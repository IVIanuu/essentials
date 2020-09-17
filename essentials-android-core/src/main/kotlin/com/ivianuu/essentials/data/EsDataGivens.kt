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

import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.MoshiSerializerFactory
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given
import kotlinx.coroutines.plus
import java.io.File

object EsDataGivens {

    @Given
    fun dataDir(): DataDir = File(androidApplicationContext.applicationInfo.dataDir)

    @Given
    fun prefsDir(): PrefsDir = given<DataDir>().resolve("prefs")

    @Given(ApplicationContext::class)
    fun moshiSerializerFactory() = MoshiSerializerFactory(given())

    @Given(ApplicationContext::class)
    fun diskDataStoreFactory() = DiskDataStoreFactory(
        scope = globalScope + dispatchers.io,
        produceBoxDirectory = { given<PrefsDir>() },
        serializerFactory = given()
    )

    @Given(ApplicationContext::class)
    fun settingsDataStoreFactory() = SettingsDataStoreFactory(
        context = androidApplicationContext,
        scope = globalScope + dispatchers.io
    )

}

typealias DataDir = File

typealias PrefsDir = File
