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

package com.ivianuu.essentials.android.settings

import android.content.ContentResolver
import android.provider.Settings
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.ImplBinding

interface AndroidSettingsAdapter<T> {
    fun get(): T
    fun set(value: T)
}

@ImplBinding
class FloatAndroidSettingsAdapter(
    private val contentResolver: ContentResolver,
    private val defaultValue: Float,
    private val name: String,
    private val type: AndroidSettingsType,
) : AndroidSettingsAdapter<Float> {
    override fun get(): Float = when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getFloat(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SECURE -> Settings.Secure.getFloat(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SYSTEM -> Settings.System.getFloat(
            contentResolver, name,
            defaultValue
        )
    }

    override fun set(value: Float) {
        when (type) {
            AndroidSettingsType.GLOBAL -> Settings.Global.putFloat(contentResolver, name, value)
            AndroidSettingsType.SECURE -> Settings.Secure.putFloat(contentResolver, name, value)
            AndroidSettingsType.SYSTEM -> Settings.System.putFloat(contentResolver, name, value)
        }
    }
}

@ImplBinding
class IntAndroidSettingsAdapter(
    private val contentResolver: ContentResolver,
    private val defaultValue: Int,
    private val name: String,
    private val type: AndroidSettingsType,
) : AndroidSettingsAdapter<Int> {
    override fun get(): Int = when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getInt(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SECURE -> Settings.Secure.getInt(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SYSTEM -> Settings.System.getInt(
            contentResolver, name,
            defaultValue
        )
    }

    override fun set(value: Int) {
        when (type) {
            AndroidSettingsType.GLOBAL -> Settings.Global.putInt(contentResolver, name, value)
            AndroidSettingsType.SECURE -> Settings.Secure.putInt(contentResolver, name, value)
            AndroidSettingsType.SYSTEM -> Settings.System.putInt(contentResolver, name, value)
        }
    }
}

@ImplBinding
class LongAndroidSettingsAdapter(
    private val contentResolver: ContentResolver,
    private val defaultValue: Long,
    private val name: String,
    private val type: AndroidSettingsType,
) : AndroidSettingsAdapter<Long> {
    override fun get(): Long = when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getLong(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SECURE -> Settings.Secure.getLong(
            contentResolver, name,
            defaultValue
        )
        AndroidSettingsType.SYSTEM -> Settings.System.getLong(
            contentResolver, name,
            defaultValue
        )
    }

    override fun set(value: Long) {
        when (type) {
            AndroidSettingsType.GLOBAL -> Settings.Global.putLong(contentResolver, name, value)
            AndroidSettingsType.SECURE -> Settings.Secure.putLong(contentResolver, name, value)
            AndroidSettingsType.SYSTEM -> Settings.System.putLong(contentResolver, name, value)
        }
    }
}

@ImplBinding
class StringAndroidSettingsAdapter(
    private val contentResolver: ContentResolver,
    private val defaultValue: String,
    private val name: String,
    private val type: AndroidSettingsType,
) : AndroidSettingsAdapter<String> {
    override fun get(): String = when (type) {
        AndroidSettingsType.GLOBAL -> Settings.Global.getString(contentResolver, name)
        AndroidSettingsType.SECURE -> Settings.Secure.getString(contentResolver, name)
        AndroidSettingsType.SYSTEM -> Settings.System.getString(contentResolver, name)
    } ?: defaultValue

    override fun set(value: String) {
        when (type) {
            AndroidSettingsType.GLOBAL -> Settings.Global.putString(contentResolver, name, value)
            AndroidSettingsType.SECURE -> Settings.Secure.putString(contentResolver, name, value)
            AndroidSettingsType.SYSTEM -> Settings.System.putString(contentResolver, name, value)
        }
    }
}
