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

package com.ivianuu.essentials.store.android.settings

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope

class SettingsBoxFactory(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {

    fun <T> create(
        name: String,
        type: SettingBox.Type,
        defaultData: T,
        adapter: SettingBox.Adapter<T>
    ): SettingBox<T> =
        SettingBoxImpl(
            type = type,
            name = name,
            defaultData = defaultData,
            adapter = adapter,
            contentResolver = context.contentResolver,
            coroutineScope = coroutineScope
        )
}

fun SettingsBoxFactory.float(
    name: String,
    type: SettingBox.Type,
    defaultData: Float = 0f
) = create(name = name, type = type, defaultData = defaultData, adapter = FloatAdapter)

private object FloatAdapter :
    SettingBox.Adapter<Float> {

    override fun get(
        name: String,
        defaultData: Float,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Float =
        when (type) {
            SettingBox.Type.Global -> Settings.Global.getFloat(
                contentResolver, name,
                defaultData
            )
            SettingBox.Type.Secure -> Settings.Secure.getFloat(
                contentResolver, name,
                defaultData
            )
            SettingBox.Type.System -> Settings.System.getFloat(
                contentResolver, name,
                defaultData
            )
        }

    override fun set(
        name: String,
        data: Float,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putFloat(contentResolver, name, data)
            SettingBox.Type.Secure -> Settings.Secure.putFloat(contentResolver, name, data)
            SettingBox.Type.System -> Settings.System.putFloat(contentResolver, name, data)
        }
    }
}

fun SettingsBoxFactory.int(
    name: String,
    type: SettingBox.Type,
    defaultData: Int = 0
) = create(name = name, type = type, defaultData = defaultData, adapter = IntAdapter)

private object IntAdapter :
    SettingBox.Adapter<Int> {

    override fun get(
        name: String,
        defaultData: Int,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Int =
        when (type) {
            SettingBox.Type.Global -> Settings.Global.getInt(
                contentResolver, name,
                defaultData
            )
            SettingBox.Type.Secure -> Settings.Secure.getInt(
                contentResolver, name,
                defaultData
            )
            SettingBox.Type.System -> Settings.System.getInt(
                contentResolver, name,
                defaultData
            )
        }

    override fun set(
        name: String,
        data: Int,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putInt(contentResolver, name, data)
            SettingBox.Type.Secure -> Settings.Secure.putInt(contentResolver, name, data)
            SettingBox.Type.System -> Settings.System.putInt(contentResolver, name, data)
        }
    }
}

fun SettingsBoxFactory.long(
    name: String,
    type: SettingBox.Type,
    defaultData: Long = 0L
) = create(name = name, type = type, defaultData = defaultData, adapter = LongAdapter)

private object LongAdapter :
    SettingBox.Adapter<Long> {

    override fun get(
        name: String,
        defaultData: Long,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Long = when (type) {
        SettingBox.Type.Global -> Settings.Global.getLong(
            contentResolver, name,
            defaultData
        )
        SettingBox.Type.Secure -> Settings.Secure.getLong(
            contentResolver, name,
            defaultData
        )
        SettingBox.Type.System -> Settings.System.getLong(
            contentResolver, name,
            defaultData
        )
    }

    override fun set(
        name: String,
        data: Long,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putLong(contentResolver, name, data)
            SettingBox.Type.Secure -> Settings.Secure.putLong(contentResolver, name, data)
            SettingBox.Type.System -> Settings.System.putLong(contentResolver, name, data)
        }
    }
}

fun SettingsBoxFactory.string(
    name: String,
    type: SettingBox.Type,
    defaultData: String = ""
) = create(name = name, type = type, defaultData = defaultData, adapter = StringAdapter)

private object StringAdapter :
    SettingBox.Adapter<String> {

    override fun get(
        name: String,
        defaultData: String,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): String {
        return when (type) {
            SettingBox.Type.Global -> Settings.Global.getString(contentResolver, name)
            SettingBox.Type.Secure -> Settings.Secure.getString(contentResolver, name)
            SettingBox.Type.System -> Settings.System.getString(contentResolver, name)
        } ?: defaultData
    }

    override fun set(
        name: String,
        data: String,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putString(contentResolver, name, data)
            SettingBox.Type.Secure -> Settings.Secure.putString(contentResolver, name, data)
            SettingBox.Type.System -> Settings.System.putString(contentResolver, name, data)
        }
    }
}
