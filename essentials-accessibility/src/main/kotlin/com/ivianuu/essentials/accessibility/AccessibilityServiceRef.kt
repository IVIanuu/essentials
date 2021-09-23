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

package com.ivianuu.essentials.accessibility

import android.app.Service
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.scope.AppScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableStateFlow

@Provide val accessibilityServiceRef: @Scoped<AppScope> MutableStateFlow<EsAccessibilityService?>
  get() = MutableStateFlow(null)

@Provide fun accessibilityServiceHolderWorker(
  holder: MutableStateFlow<EsAccessibilityService?>,
  service: Service
): ScopeWorker<AccessibilityScope> = {
  holder.value = service as EsAccessibilityService
  onCancel { holder.value = null }
}
