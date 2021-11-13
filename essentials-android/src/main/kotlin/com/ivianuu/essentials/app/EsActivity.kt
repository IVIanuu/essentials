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
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalComponent
import com.ivianuu.essentials.ui.LocalUiComponent
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.essentials.ui.UiComponentFactory
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.util.ForegroundActivityMarker
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.android.appComponent
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.dispose
import com.ivianuu.injekt.common.entryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class EsActivity : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback(this) {
      finish()
    }

    // initialize activity component
    activityComponent

    val component = appComponent.entryPoint<UiComponentFactory>().uiComponent()
    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { component.dispose() }
    }

    val esActivityComponent = component.entryPoint<EsActivityComponent>()

    setContent {
      CompositionLocalProvider(
        LocalComponent provides component,
        LocalUiComponent provides component
      ) {
        esActivityComponent.decorateUi {
          esActivityComponent.appUi()
        }
      }
    }
  }
}

@EntryPoint<UiComponent> interface EsActivityComponent {
  val appUi: AppUi
  val decorateUi: DecorateUi
}
