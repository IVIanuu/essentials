package com.ivianuu.essentials.twilight

import com.ivianuu.kprefs.common.PrefValueHolder

enum class TwilightMode(override val value: String) : PrefValueHolder<String> {
    LIGHT("light"),
    DARK("dark"),
    BATTERY("battery"),
    TIME("time"),
    SYSTEM("system")
}