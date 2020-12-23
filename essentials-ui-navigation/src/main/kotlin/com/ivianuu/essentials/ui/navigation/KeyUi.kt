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

package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
inline fun <reified K : Key, T : @Composable () -> Unit> keyUiBinding():
        @GivenSetElement (@Given (K) -> T) -> KeyUiFactoryBinding = {
            K::class to it as (Key) -> @Composable () -> Unit
}

typealias KeyUiFactoryBinding = Pair<KClass<out Key>, (Key) -> @Composable () -> Unit>

