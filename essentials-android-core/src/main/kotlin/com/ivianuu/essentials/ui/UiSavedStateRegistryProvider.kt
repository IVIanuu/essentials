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

package com.ivianuu.essentials.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.ui.platform.LocalContext
import com.ivianuu.injekt.Given

typealias SavableStateRegistryProvider = UiDecorator

@UiDecoratorBinding
@Given
fun savableStateRegistryProvider(): SavableStateRegistryProvider = { content ->
    val activity = LocalContext.current as? ComponentActivity
    if (activity != null) {
        CompositionLocalProvider(
            LocalSaveableStateRegistry provides SaveableStateRegistry(emptyMap()) { true },
            content = content
        )
    }
}