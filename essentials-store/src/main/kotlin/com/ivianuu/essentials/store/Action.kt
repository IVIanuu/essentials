/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.sourcekey.memo
import com.ivianuu.essentials.sourcekey.sourceKeyOf
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.ApplicationScoped
import com.ivianuu.injekt.component.Storage
import com.ivianuu.injekt.component.memo
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.typeOf

internal typealias MutableActions<T>  = EventFlow<T>

@Given inline fun <reified T> mutableActions(
    @Given storage: Storage<ApplicationScoped>
): MutableActions<T> = storage.memo(sourceKeyOf(typeOf<T>())) { EventFlow() }

typealias Actions<T> = Flow<T>
@Given inline val <T> @Given MutableActions<T>.actions: Actions<T>
    get() = this

typealias DispatchAction<T> = (T) -> Unit
@Given inline val <T> @Given MutableActions<T>.dispatchAction: DispatchAction<T>
    get() = this::emit
