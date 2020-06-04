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

package com.ivianuu.essentials.ui.injekt

import androidx.compose.Composable
import androidx.compose.remember
import com.ivianuu.essentials.ui.common.compositionActivity
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.composition.get

@Composable
val compositionComponent: ActivityComponent
    get() = compositionActivity.activityComponent

@Composable
fun <T> inject(): T {
    val component = compositionComponent
    return remember(component) { component.get() }
}
