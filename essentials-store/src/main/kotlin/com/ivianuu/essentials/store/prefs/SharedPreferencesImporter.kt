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

package com.ivianuu.essentials.store.prefs

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

class SharedPreferencesImporter(
    private val boxFactory: PrefBoxFactory,
    private val context: Context,
    private val packageName: String,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun importAndDeleteDefaultIfNeeded() = importAndDeleteIfNeeded(
        packageName + "_preferences"
    )

    suspend fun importAndDeleteIfNeeded(name: String): Unit = withContext(dispatcher) {
        val prefsFile = File(context.applicationInfo.dataDir, "shared_prefs/$name.xml")
        if (!prefsFile.exists()) return@withContext

        val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

        sharedPreferences.all.forEach { (key, value) ->
            when (value) {
                is Boolean -> boxFactory.boolean(key).set(value)
                is Float -> boxFactory.float(key).set(value)
                is Int -> boxFactory.int(key).set(value)
                is Long -> boxFactory.long(key).set(value)
                is String -> boxFactory.string(key).set(value)
                is Set<*> -> boxFactory.stringSet(key).set(value as Set<String>)
            }
        }

        prefsFile.delete()
    }

    suspend fun importAndDeleteAsStringMapIfNeeded(name: String): Unit =
        withContext(dispatcher) {
            val prefsFile = File(context.applicationInfo.dataDir, "shared_prefs/$name.xml")
            if (!prefsFile.exists()) return@withContext
            val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val box = boxFactory.stringMap(name)
            box.set(sharedPreferences.all as Map<String, String>)
            prefsFile.delete()
        }
}
