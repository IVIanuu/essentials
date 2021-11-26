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
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalElements
import com.ivianuu.essentials.ui.LocalUiElements
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.util.ForegroundActivityMarker
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class EsActivity : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback {
      finish()
    }

    val uiScope = Scope<UiScope>()

    val uiComponent = application
      .cast<AppElementsOwner>()
      .appElements<EsActivityComponent>()
      .uiComponent(uiScope, this)

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    setContent {
      CompositionLocalProvider(
        LocalElements provides uiComponent.elements,
        LocalUiElements provides uiComponent.elements
      ) {
        uiComponent.decorateUi {
          uiComponent.appUi()
        }
      }
    }
  }
}

@Provide @Element<AppScope>
data class EsActivityComponent(
  val uiComponent: (Scope<UiScope>, ComponentActivity) -> UiComponent
)

@Provide data class UiComponent(
  val appUi: AppUi,
  val decorateUi: DecorateUi,
  val elements: Elements<UiScope>
)

