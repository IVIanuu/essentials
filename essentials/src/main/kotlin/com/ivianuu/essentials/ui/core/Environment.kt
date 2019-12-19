/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.core

import androidx.activity.ComponentActivity
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.remember
import androidx.ui.core.CoroutineContextAmbient
import androidx.ui.core.FocusManagerAmbient
import com.ivianuu.essentials.ui.common.KeyboardManager
import com.ivianuu.essentials.ui.common.KeyboardManagerAmbient
import com.ivianuu.essentials.ui.common.MultiAmbientProvider
import com.ivianuu.essentials.ui.common.with
import com.ivianuu.essentials.ui.injekt.ComponentAmbient
import com.ivianuu.injekt.Component
import kotlin.coroutines.CoroutineContext

@Composable
fun EsEnvironment(
    activity: ComponentActivity,
    component: Component,
    coroutineContext: CoroutineContext,
    children: @Composable() () -> Unit
) {
    val focusManager = ambient(FocusManagerAmbient)
    MultiAmbientProvider(
        ActivityAmbient with activity,
        ComponentAmbient with component,
        CoroutineContextAmbient with coroutineContext,
        KeyboardManagerAmbient with remember { KeyboardManager(focusManager, activity) }
    ) {
        WindowInsetsManager {
            SystemBarManager {
                ConfigurationFix {
                    OrientationProvider(children = children)
                }
            }
        }
    }
}
