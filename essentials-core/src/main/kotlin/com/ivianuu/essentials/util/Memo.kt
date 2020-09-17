package com.ivianuu.essentials.util

import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.ApplicationContext
import com.ivianuu.injekt.common.alias
import com.ivianuu.injekt.given
import com.ivianuu.injekt.keyOf
import com.ivianuu.injekt.scoped

@Reader
inline fun <T> memo(
    vararg inputs: Any?,
    key: Any = sourceKeyOf(),
    init: () -> T
): T {
    val memoStore = given<MemoStore>()
    return synchronized(memoStore) {
        var value = memoStore[key] as? MemoValue<T>
        if (value != null && !value.inputs.contentEquals(inputs)) {
            value = null
        }

        if (value == null) {
            value = MemoValue(init(), inputs)
            memoStore[key] = value
        }
        value.value
    }
}

@Module(ApplicationContext::class)
fun ContextBuilder.memoModule() {
    scoped<MemoStore> { mutableMapOf() }
    if (contextName != null) {
        alias(
            t = keyOf<MemoStore>(),
            s = qualifiedKeyOf<MemoStore>(contextName)
        )
    }
}

internal typealias MemoStore = MutableMap<Any, MemoValue<*>>

@PublishedApi
internal class MemoValue<T>(
    val value: T,
    val inputs: Array<out Any?>
)
