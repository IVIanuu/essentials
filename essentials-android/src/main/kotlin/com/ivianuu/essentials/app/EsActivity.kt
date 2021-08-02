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

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*

class EsActivity : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val uiScope = activityScope.element<@ChildScopeFactory () -> UiScope>()()
    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      runOnCancellation {
        uiScope.cast<DisposableScope>().dispose()
      }
    }

    val component = uiScope.element<EsActivityComponent>()

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
