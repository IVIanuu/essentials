package com.ivianuu.essentials.datastore.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.essentials.datastore.map
import kotlin.time.Duration

// todo remove once moshi supports inline classes

fun DiskDataStoreFactory.color(
    name: String,
    produceDefaultData: () -> Color
): DataStore<Color> {
    return create(name = name, produceDefaultData = { produceDefaultData().toArgb() })
        .map(
            fromRaw = { Color(it) },
            toRaw = { it.toArgb() }
        )
}

fun DiskDataStoreFactory.duration(
    name: String,
    produceDefaultData: () -> Duration
): DataStore<Duration> {
    return create(name = name, produceDefaultData = { produceDefaultData().toDouble() })
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
