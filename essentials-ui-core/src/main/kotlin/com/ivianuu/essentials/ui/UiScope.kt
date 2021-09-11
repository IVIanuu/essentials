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

package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ActivityScope
import com.ivianuu.injekt.scope.ChildScopeModule0
import com.ivianuu.injekt.scope.Scope

val LocalScope = compositionLocalOf<Scope> { error("No scope provided") }

@Provide val composableScope: Scope
  @Composable get() = LocalScope.current

typealias UiScope = Scope

@Provide val uiScopeModule = ChildScopeModule0<ActivityScope, UiScope>()
