package com.ivianuu.essentials.util

import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.get
import kotlinx.coroutines.CoroutineScope

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class GlobalScope

@Reader
val globalScope: @GlobalScope CoroutineScope
    get() = get()
