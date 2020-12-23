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
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.staticAmbientOf
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.android.ActivityRetainedScoped
import com.ivianuu.injekt.android.ActivityScoped
import com.ivianuu.injekt.component.ApplicationScoped
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.ComponentKey
import com.ivianuu.injekt.component.componentElement

object UiScoped : Component.Name

val UiComponentFactoryKey =
    ComponentKey<() -> Component<UiScoped>>()

@GivenSetElement fun uiComponentFactory(
    @Given parent: Component<UiScoped>,
    @Given builderFactory: () -> Component.Builder<UiScoped>,
) = componentElement(ActivityScoped, UiComponentFactoryKey) {
    builderFactory()
        .dependency(parent)
        .build()
}

val AmbientUiComponent = staticAmbientOf<Component<UiScoped>> { error("No UiComponent installed") }
