package com.ivianuu.essentials.store.android.settings

import android.content.ContentResolver
import com.ivianuu.essentials.store.Box
import com.ivianuu.ksettings.Setting

interface SettingBox<T> : Box<T> {

    enum class Type {
        GLOBAL, SECURE, SYSTEM
    }

    interface Adapter<T> {
        fun get(
            name: String,
            defaultValue: T,
            contentResolver: ContentResolver,
            type: Setting.Type
        ): T

        fun set(
            name: String,
            value: T,
            contentResolver: ContentResolver,
            type: Setting.Type
        )
    }

}