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

package com.ivianuu.essentials.util

import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

typealias GlobalScope = CoroutineScope

@Binding(ApplicationComponent::class)
fun globalScope(defaultDispatcher: DefaultDispatcher): GlobalScope =
    CoroutineScope(defaultDispatcher)

@FunBinding
suspend fun globalTask(
    globalScope: GlobalScope,
    block: @Assisted suspend CoroutineScope.() -> Unit,
) {
    globalScope.launch(block = block).join()
}
