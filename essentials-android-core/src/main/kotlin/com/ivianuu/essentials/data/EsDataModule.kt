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
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.MoshiSerializerFactory
import com.ivianuu.essentials.datastore.android.settings.SettingsDataStoreFactory
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import java.io.File

object EsDataModule {

    @Given
    @Reader
    fun dataDir(): DataDir = applicationContext.applicationInfo.dataDir

    @Given
    @Reader
    fun prefsDir(): PrefsDir = "${given<DataDir>()}/prefs"

    @Given(ApplicationComponent::class)
    @Reader
    fun moshiSerializerFactory() = MoshiSerializerFactory(given())

    @Given(ApplicationComponent::class)
    @Reader
    fun diskDataStoreFactory() = DiskDataStoreFactory(
        scope = globalScope + dispatchers.io,
        produceBoxDirectory = { File(given<PrefsDir>()) },
        serializerFactory = given()
    )

    @Given
    @Reader
    fun settingsDataStoreFactory() = SettingsDataStoreFactory(
        context = applicationContext,
        scope = globalScope + dispatchers.io
    )

}

@Distinct
typealias DataDir = String

@Distinct
typealias PrefsDir = String
