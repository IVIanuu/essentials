package com.ivianuu.essentials.store.android

import androidx.ui.graphics.Color
import androidx.ui.graphics.toArgb
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.map
import kotlin.time.Duration

// todo remove once moshi supports inline classes

fun PrefBoxFactory.color(
    name: String,
    defaultData: Color
): Box<Color> {
    return create(name = name, defaultData = defaultData.toArgb())
        .map(
            fromRaw = { Color(it) },
            toRaw = { it.toArgb() }
        )
}

fun PrefBoxFactory.duration(
    name: String,
    defaultData: Duration
): Box<Duration> {
    return create(name = name, defaultData = defaultData.toDouble())
        .map(
            fromRaw = { it.toDuration() },
            toRaw = { it.toDouble() }
        )
}

private fun Double.toDuration(): Duration {
    return Duration::class.java.getDeclaredConstructor(Double::class.java)
        .also { it.isAccessible = true }
        .newInstance(this)
}

private fun Duration.toDouble(): Double {
    return javaClass.declaredFields
        .first { it.type == Double::class.java }
        .also { it.isAccessible = true }
        .get(this)!! as Double
}