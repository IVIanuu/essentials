/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.essentials.ui.app.*
import com.ivianuu.essentials.util.*
import kotlinx.coroutines.*

class EsActivity : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback {
      finish()
    }

    val uiScope = Scope<UiScope>()

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    val uiComponent = application
      .let { it as AppElementsOwner }
      .appElements<EsActivityComponent>()
      .uiComponent(uiScope, this)

    setContent {
      CompositionLocalProvider(LocalUiElements provides uiComponent.elements) {
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
