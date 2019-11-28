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

package com.ivianuu.essentials.store.settings

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Factory

@Factory
class SettingsBoxFactory(
    private val context: Context,
    private val dispatchers: AppDispatchers
) {

    fun <T> setting(
        name: String,
        type: SettingBox.Type,
        defaultValue: T,
        adapter: SettingBox.Adapter<T>
    ): SettingBox<T> = SettingBoxImpl(
        type = type,
        name = name,
        defaultValue = defaultValue,
        adapter = adapter,
        contentResolver = context.contentResolver,
        dispatcher = dispatchers.io
    )

}

fun SettingsBoxFactory.float(
    name: String,
    type: SettingBox.Type,
    defaultValue: Float = 0f
) = setting(name = name, type = type, defaultValue = defaultValue, adapter = FloatAdapter)

private object FloatAdapter : SettingBox.Adapter<Float> {

    override fun get(
        name: String,
        defaultValue: Float,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Float =
        when (type) {
            SettingBox.Type.Global -> Settings.Global.getFloat(
                contentResolver, name,
                defaultValue
            )
            SettingBox.Type.Secure -> Settings.Secure.getFloat(
                contentResolver, name,
                defaultValue
            )
            SettingBox.Type.System -> Settings.System.getFloat(
                contentResolver, name,
                defaultValue
            )
        }

    override fun set(
        name: String,
        value: Float,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putFloat(contentResolver, name, value)
            SettingBox.Type.Secure -> Settings.Secure.putFloat(contentResolver, name, value)
            SettingBox.Type.System -> Settings.System.putFloat(contentResolver, name, value)
        }
    }
}

fun SettingsBoxFactory.int(
    name: String,
    type: SettingBox.Type,
    defaultValue: Int = 0
) = setting(name = name, type = type, defaultValue = defaultValue, adapter = IntAdapter)

private object IntAdapter : SettingBox.Adapter<Int> {

    override fun get(
        name: String,
        defaultValue: Int,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Int =
        when (type) {
            SettingBox.Type.Global -> Settings.Global.getInt(
                contentResolver, name,
                defaultValue
            )
            SettingBox.Type.Secure -> Settings.Secure.getInt(
                contentResolver, name,
                defaultValue
            )
            SettingBox.Type.System -> Settings.System.getInt(
                contentResolver, name,
                defaultValue
            )
        }

    override fun set(
        name: String,
        value: Int,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putInt(contentResolver, name, value)
            SettingBox.Type.Secure -> Settings.Secure.putInt(contentResolver, name, value)
            SettingBox.Type.System -> Settings.System.putInt(contentResolver, name, value)
        }
    }
}

fun SettingsBoxFactory.long(
    name: String,
    type: SettingBox.Type,
    defaultValue: Long = 0L
) = setting(name = name, type = type, defaultValue = defaultValue, adapter = LongAdapter)

private object LongAdapter : SettingBox.Adapter<Long> {

    override fun get(
        name: String,
        defaultValue: Long,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): Long = when (type) {
        SettingBox.Type.Global -> Settings.Global.getLong(
            contentResolver, name,
            defaultValue
        )
        SettingBox.Type.Secure -> Settings.Secure.getLong(
            contentResolver, name,
            defaultValue
        )
        SettingBox.Type.System -> Settings.System.getLong(
            contentResolver, name,
            defaultValue
        )
    }

    override fun set(
        name: String,
        value: Long,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putLong(contentResolver, name, value)
            SettingBox.Type.Secure -> Settings.Secure.putLong(contentResolver, name, value)
            SettingBox.Type.System -> Settings.System.putLong(contentResolver, name, value)
        }
    }
}

fun SettingsBoxFactory.string(
    name: String,
    type: SettingBox.Type,
    defaultValue: String = ""
) = setting(name = name, type = type, defaultValue = defaultValue, adapter = StringAdapter)

private object StringAdapter : SettingBox.Adapter<String> {

    override fun get(
        name: String,
        defaultValue: String,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ): String {
        return when (type) {
            SettingBox.Type.Global -> Settings.Global.getString(contentResolver, name)
            SettingBox.Type.Secure -> Settings.Secure.getString(contentResolver, name)
            SettingBox.Type.System -> Settings.System.getString(contentResolver, name)
        } ?: defaultValue
    }

    override fun set(
        name: String,
        value: String,
        contentResolver: ContentResolver,
        type: SettingBox.Type
    ) {
        when (type) {
            SettingBox.Type.Global -> Settings.Global.putString(contentResolver, name, value)
            SettingBox.Type.Secure -> Settings.Secure.putString(contentResolver, name, value)
            SettingBox.Type.System -> Settings.System.putString(contentResolver, name, value)
        }
    }
}