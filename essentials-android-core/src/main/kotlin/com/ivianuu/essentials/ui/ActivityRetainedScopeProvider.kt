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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ivianuu.essentials.ui.common.LocalRetainedScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.Scope

typealias ActivityRetainedScopeProvider = UiDecorator

@UiDecoratorBinding
@Given
fun activityRetainedScopeProvider(): ActivityRetainedScopeProvider = { content ->
    val activity = LocalContext.current as? ComponentActivity
    if (activity != null) {
        val retainedScope = remember { Scope() }
        CompositionLocalProvider(
            LocalRetainedScope provides retainedScope,
            content = content
        )
        DisposableEffect(true) {
            onDispose { retainedScope.dispose() }
        }
    }
}
