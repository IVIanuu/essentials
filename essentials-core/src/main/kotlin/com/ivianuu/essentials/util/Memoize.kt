package com.ivianuu.essentials.util

import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@Reader
inline fun <T> memoize(
    store: MemoizeStore = given<ApplicationMemoizeStore>(),
    init: () -> T
): T = _memoize(sourceKeyOf(), store, init)

@Reader
inline fun <T> memoize(
    vararg inputs: Any?,
    store: MemoizeStore = given<ApplicationMemoizeStore>(),
    init: () -> T
): T = _memoize(sourceKeyOf(*inputs), store, init)

@PublishedApi
internal inline fun <T> _memoize(key: Any, store: MemoizeStore, init: () -> T): T {
    store[key]?.let { return it as T }
    return synchronized(store) { store.getOrPut(key, init) as T }
}

typealias MemoizeStore = MutableMap<Any?, Any?>

typealias ApplicationMemoizeStore = MemoizeStore

@Given(ApplicationContext::class)
fun givenApplicationMemoizeStore(): ApplicationMemoizeStore = mutableMapOf()
