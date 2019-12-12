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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import com.ivianuu.essentials.ui.common.UrlRoute
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.coroutines.coroutineScope
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun navigateOnClick(route: () -> Route): () -> Unit {
    val navigator = inject<Navigator>()
    return remember { { navigator.push(route()) } }
}

@Composable
fun openUrlOnClick(url: () -> String) = navigateOnClick { UrlRoute(url()) }

@Composable
fun launchOnClick(
    block: suspend CoroutineScope.() -> Unit
): () -> Unit {
    val coroutineScope = coroutineScope()
    return remember {
        {
            coroutineScope.launch(block = block)
        }
    }
}
