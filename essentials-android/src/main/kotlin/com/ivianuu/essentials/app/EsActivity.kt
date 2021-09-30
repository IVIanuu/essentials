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

package com.ivianuu.essentials.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalScope
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.core.AppUi
import com.ivianuu.essentials.util.ForegroundActivityMarker
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Providers
import com.ivianuu.injekt.scope.ChildScopeFactory
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class EsActivity : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    @Provide val uiScope = @Providers("com.ivianuu.injekt.android.activityScope")
    requireElement<@ChildScopeFactory () -> UiScope>()()
    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    val component = requireElement<EsActivityComponent>()

    setContent {
      CompositionLocalProvider(LocalScope provides uiScope) {
        component.decorateUi {
          component.appUi()
        }
      }
    }
  }
}

@Provide @ScopeElement<UiScope>
class EsActivityComponent(val appUi: AppUi, val decorateUi: DecorateUi)
