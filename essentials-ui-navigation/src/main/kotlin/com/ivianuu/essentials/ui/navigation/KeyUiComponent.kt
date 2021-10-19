/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.EntryPoint

val LocalKeyUiComponent = compositionLocalOf<KeyUiComponent> { error("No key ui component provided") }

@Component interface KeyUiComponent

@EntryPoint<UiComponent> interface KeyUiComponentFactory {
  fun keyUiComponent(key: Key<*>): KeyUiComponent
}