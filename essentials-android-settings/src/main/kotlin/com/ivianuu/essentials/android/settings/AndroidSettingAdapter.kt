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

import android.content.*
import android.provider.*
import com.ivianuu.injekt.*

interface AndroidSettingAdapter<T> {
  fun get(): T
  fun set(value: T)
}

@Given class FloatAndroidSettingAdapter(
  @Given private val contentResolver: ContentResolver,
  @Given private val defaultValue: Float,
  @Given private val name: String,
  @Given private val type: AndroidSettingsType,
) : AndroidSettingAdapter<Float> {
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

@Given class IntAndroidSettingAdapter(
  @Given private val contentResolver: ContentResolver,
  @Given private val defaultValue: Int,
  @Given private val name: String,
  @Given private val type: AndroidSettingsType,
) : AndroidSettingAdapter<Int> {
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

@Given class LongAndroidSettingAdapter(
  @Given private val contentResolver: ContentResolver,
  @Given private val defaultValue: Long,
  @Given private val name: String,
  @Given private val type: AndroidSettingsType,
) : AndroidSettingAdapter<Long> {
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

@Given class StringAndroidSettingAdapter(
  @Given private val contentResolver: ContentResolver,
  @Given private val defaultValue: String,
  @Given private val name: String,
  @Given private val type: AndroidSettingsType,
) : AndroidSettingAdapter<String> {
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
