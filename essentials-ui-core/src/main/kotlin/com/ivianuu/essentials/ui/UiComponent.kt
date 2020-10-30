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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.merge.MergeChildComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent

@MergeChildComponent
interface UiComponent

@UiDecoratorBinding
@FunBinding
@Composable
fun ProvideUiComponent(children: @Assisted @Composable () -> Unit) {
    val activity = compositionActivity
    val uiComponent = remember {
        activity.activityComponent.mergeComponent<UiComponentFactoryOwner>()
            .uiComponentFactory()
    }
    Providers(
        UiComponentAmbient provides uiComponent,
        children = children
    )
}

@MergeInto(ActivityComponent::class)
interface UiComponentFactoryOwner {
    val uiComponentFactory: () -> UiComponent
}

val UiComponentAmbient = ambientOf<UiComponent> { error("No UiComponent installed") }
