/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util

import com.ivianuu.autodispose.ScopeProvider
import com.ivianuu.autodispose.lifecycle.LifecycleScopeProvider
import io.reactivex.Completable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A lazy property that gets cleaned up when the scope provider emits
 */
class AutoClearedValue<T>(
    scope: Completable,
    initialValue: T? = null
) : ReadWriteProperty<Any, T> {

    private var _value: T? = null

    init {
        _value = initialValue

        scope.subscribe { _value = null }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        _value ?: throw IllegalStateException(
            "should never call auto-cleared-value get when it might not be available"
        )

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        _value = value
    }
}

fun <T> ScopeProvider.autoCleared(scope: Completable, initialValue: T? = null) =
    AutoClearedValue(scope, initialValue)

fun <T> ScopeProvider.autoCleared(initialValue: T? = null) =
    autoCleared(requestScope(), initialValue)

fun <T> LifecycleScopeProvider<*>.autoCleared(initialValue: T? = null) =
    AutoClearedValue(requestScope(), initialValue)

fun <T, E> LifecycleScopeProvider<E>.autoCleared(untilEvent: E, initialValue: T? = null) =
    AutoClearedValue(requestScope(untilEvent), initialValue)