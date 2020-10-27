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

package com.ivianuu.essentials.datastore.android.settings

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.datastore.DataStore
import kotlinx.coroutines.CoroutineScope

class SettingsDataStoreFactory(
    private val context: Context,
    private val mainDispatcher: MainDispatcher,
    private val scope: CoroutineScope,
) {
    fun <T> create(
        name: String,
        type: SettingDataStore.Type,
        defaultData: T,
        adapter: SettingDataStore.Adapter<T>
    ): DataStore<T> = SettingDataStoreImpl(
        type = type,
        name = name,
        defaultData = defaultData,
        adapter = adapter,
        contentResolver = context.contentResolver,
        mainDispatcher = mainDispatcher,
        scope = scope
    )
}

fun SettingsDataStoreFactory.float(
    name: String,
    type: SettingDataStore.Type,
    defaultData: Float = 0f
) = create(name = name, type = type, defaultData = defaultData, adapter = FloatAdapter)

private object FloatAdapter : SettingDataStore.Adapter<Float> {

    override fun get(
        name: String,
        defaultData: Float,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ): Float =
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.getFloat(
                contentResolver, name,
                defaultData
            )
            SettingDataStore.Type.Secure -> Settings.Secure.getFloat(
                contentResolver, name,
                defaultData
            )
            SettingDataStore.Type.System -> Settings.System.getFloat(
                contentResolver, name,
                defaultData
            )
        }

    override fun set(
        name: String,
        data: Float,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ) {
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.putFloat(contentResolver, name, data)
            SettingDataStore.Type.Secure -> Settings.Secure.putFloat(contentResolver, name, data)
            SettingDataStore.Type.System -> Settings.System.putFloat(contentResolver, name, data)
        }
    }
}

fun SettingsDataStoreFactory.int(
    name: String,
    type: SettingDataStore.Type,
    defaultData: Int = 0
) = create(name = name, type = type, defaultData = defaultData, adapter = IntAdapter)

private object IntAdapter : SettingDataStore.Adapter<Int> {

    override fun get(
        name: String,
        defaultData: Int,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ): Int =
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.getInt(
                contentResolver, name,
                defaultData
            )
            SettingDataStore.Type.Secure -> Settings.Secure.getInt(
                contentResolver, name,
                defaultData
            )
            SettingDataStore.Type.System -> Settings.System.getInt(
                contentResolver, name,
                defaultData
            )
        }

    override fun set(
        name: String,
        data: Int,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ) {
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.putInt(contentResolver, name, data)
            SettingDataStore.Type.Secure -> Settings.Secure.putInt(contentResolver, name, data)
            SettingDataStore.Type.System -> Settings.System.putInt(contentResolver, name, data)
        }
    }
}

fun SettingsDataStoreFactory.long(
    name: String,
    type: SettingDataStore.Type,
    defaultData: Long = 0L
) = create(name = name, type = type, defaultData = defaultData, adapter = LongAdapter)

private object LongAdapter : SettingDataStore.Adapter<Long> {

    override fun get(
        name: String,
        defaultData: Long,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ): Long = when (type) {
        SettingDataStore.Type.Global -> Settings.Global.getLong(
            contentResolver, name,
            defaultData
        )
        SettingDataStore.Type.Secure -> Settings.Secure.getLong(
            contentResolver, name,
            defaultData
        )
        SettingDataStore.Type.System -> Settings.System.getLong(
            contentResolver, name,
            defaultData
        )
    }

    override fun set(
        name: String,
        data: Long,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ) {
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.putLong(contentResolver, name, data)
            SettingDataStore.Type.Secure -> Settings.Secure.putLong(contentResolver, name, data)
            SettingDataStore.Type.System -> Settings.System.putLong(contentResolver, name, data)
        }
    }
}

fun SettingsDataStoreFactory.string(
    name: String,
    type: SettingDataStore.Type,
    defaultData: String = ""
) = create(name = name, type = type, defaultData = defaultData, adapter = StringAdapter)

private object StringAdapter : SettingDataStore.Adapter<String> {

    override fun get(
        name: String,
        defaultData: String,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ): String {
        return when (type) {
            SettingDataStore.Type.Global -> Settings.Global.getString(contentResolver, name)
            SettingDataStore.Type.Secure -> Settings.Secure.getString(contentResolver, name)
            SettingDataStore.Type.System -> Settings.System.getString(contentResolver, name)
        } ?: defaultData
    }

    override fun set(
        name: String,
        data: String,
        contentResolver: ContentResolver,
        type: SettingDataStore.Type
    ) {
        when (type) {
            SettingDataStore.Type.Global -> Settings.Global.putString(contentResolver, name, data)
            SettingDataStore.Type.Secure -> Settings.Secure.putString(contentResolver, name, data)
            SettingDataStore.Type.System -> Settings.System.putString(contentResolver, name, data)
        }
    }
}
