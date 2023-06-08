/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateUi
import com.ivianuu.essentials.ui.LocalUiElements
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.util.ForegroundActivityMarker
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Elements
import com.ivianuu.injekt.common.Scope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@Provide @AndroidComponent class EsActivity(
  private val uiComponent: (Scope<UiScope>, ComponentActivity) -> UiComponent
) : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback {
      finish()
    }

    val uiScope = Scope<UiScope>()

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    val uiComponent = uiComponent(uiScope, this)

    setContent {
      CompositionLocalProvider(LocalUiElements provides uiComponent.elements) {
        uiComponent.decorateUi {
          uiComponent.appUi()
        }
      }
    }
  }
}

@Provide data class UiComponent(
  val appUi: AppUi,
  val decorateUi: DecorateUi,
  val elements: Elements<UiScope>
)
