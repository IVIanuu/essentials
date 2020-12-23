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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext
import com.ivianuu.essentials.ui.common.RetainedObjects
import com.ivianuu.essentials.ui.common.AmbientRetainedObjects
import com.ivianuu.essentials.ui.core.currentOrNull
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenGroup

@GivenGroup val provideActivityRetainedObjectsBinding =
    uiDecoratorBinding<ProvideActivityRetainedObjects>("activity_retained_objects")

@GivenFun @Composable
fun ProvideActivityRetainedObjects(content: @Composable () -> Unit) {
    val activity = AmbientContext.currentOrNull as? ComponentActivity
    if (activity != null) {
        val retainedObjects = remember { RetainedObjects() }
        Providers(
            AmbientRetainedObjects provides retainedObjects,
            content = content
        )
        onDispose { retainedObjects.dispose() }
    }
}
