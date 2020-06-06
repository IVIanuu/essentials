package com.ivianuu.essentials.store.android

import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.android.prefs.PrefBoxFactory
import com.ivianuu.essentials.store.map
import kotlin.time.Duration

// todo remove once moshi supports inline classes

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
