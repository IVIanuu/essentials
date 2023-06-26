/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import android.content.ContentResolver
import android.provider.Settings
import com.ivianuu.injekt.Provide

interface AndroidSettingAdapter<T> {
  fun get(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    defaultValue: T
  ): T

  fun set(contentResolver: ContentResolver, name: String, type: AndroidSettingsType, value: T)
}

@Provide object FloatAndroidSettingAdapter : AndroidSettingAdapter<Float> {
  override fun get(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    defaultValue: Float
  ) = when (type) {
    AndroidSettingsType.GLOBAL -> Settings.Global.getFloat(contentResolver, name, defaultValue)
    AndroidSettingsType.SECURE -> Settings.Secure.getFloat(contentResolver, name, defaultValue)
    AndroidSettingsType.SYSTEM -> Settings.System.getFloat(contentResolver, name, defaultValue)
  }

  override fun set(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    value: Float
  ) {
    when (type) {
      AndroidSettingsType.GLOBAL -> Settings.Global.putFloat(contentResolver, name, value)
      AndroidSettingsType.SECURE -> Settings.Secure.putFloat(contentResolver, name, value)
      AndroidSettingsType.SYSTEM -> Settings.System.putFloat(contentResolver, name, value)
    }
  }
}

@Provide object IntAndroidSettingAdapter : AndroidSettingAdapter<Int> {
  override fun get(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    defaultValue: Int
  ) = when (type) {
    AndroidSettingsType.GLOBAL -> Settings.Global.getInt(contentResolver, name, defaultValue)
    AndroidSettingsType.SECURE -> Settings.Secure.getInt(contentResolver, name, defaultValue)
    AndroidSettingsType.SYSTEM -> Settings.System.getInt(contentResolver, name, defaultValue)
  }

  override fun set(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    value: Int
  ) {
    when (type) {
      AndroidSettingsType.GLOBAL -> Settings.Global.putInt(contentResolver, name, value)
      AndroidSettingsType.SECURE -> Settings.Secure.putInt(contentResolver, name, value)
      AndroidSettingsType.SYSTEM -> Settings.System.putInt(contentResolver, name, value)
    }
  }
}

@Provide object LongAndroidSettingAdapter : AndroidSettingAdapter<Long> {
  override fun get(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    defaultValue: Long
  ) = when (type) {
    AndroidSettingsType.GLOBAL -> Settings.Global.getLong(contentResolver, name, defaultValue)
    AndroidSettingsType.SECURE -> Settings.Secure.getLong(contentResolver, name, defaultValue)
    AndroidSettingsType.SYSTEM -> Settings.System.getLong(contentResolver, name, defaultValue)
  }

  override fun set(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    value: Long
  ) {
    when (type) {
      AndroidSettingsType.GLOBAL -> Settings.Global.putLong(contentResolver, name, value)
      AndroidSettingsType.SECURE -> Settings.Secure.putLong(contentResolver, name, value)
      AndroidSettingsType.SYSTEM -> Settings.System.putLong(contentResolver, name, value)
    }
  }
}

@Provide object StringAndroidSettingAdapter : AndroidSettingAdapter<String> {
  override fun get(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    defaultValue: String
  ) = when (type) {
    AndroidSettingsType.GLOBAL -> Settings.Global.getString(contentResolver, name)
    AndroidSettingsType.SECURE -> Settings.Secure.getString(contentResolver, name)
    AndroidSettingsType.SYSTEM -> Settings.System.getString(contentResolver, name)
  } ?: defaultValue

  override fun set(
    contentResolver: ContentResolver,
    name: String,
    type: AndroidSettingsType,
    value: String
  ) {
    when (type) {
      AndroidSettingsType.GLOBAL -> Settings.Global.putString(contentResolver, name, value)
      AndroidSettingsType.SECURE -> Settings.Secure.putString(contentResolver, name, value)
      AndroidSettingsType.SYSTEM -> Settings.System.putString(contentResolver, name, value)
    }
  }
}
