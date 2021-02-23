package com.ivianuu.essentials.util

import com.ivianuu.injekt.Given

@Given
fun <K, V> setToMap(
    @Given set: Set<Pair<K, V>>
): Map<K, V> = set.toMap()
