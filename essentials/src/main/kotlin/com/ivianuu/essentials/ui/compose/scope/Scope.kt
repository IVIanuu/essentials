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

package com.ivianuu.essentials.ui.compose.scope

import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.onDispose
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

@Composable
fun scope(): Scope {
    val scope = remember { MutableScope() }
    onDispose { scope.close() }
    return scope
}
