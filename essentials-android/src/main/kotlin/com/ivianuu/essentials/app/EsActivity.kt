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
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateAppUi
import com.ivianuu.essentials.ui.LocalScope
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.essentials.util.ForegroundActivityMarker
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@Provide @AndroidComponent class EsActivity(
  private val uiScopeFactory: (ComponentActivity) -> Scope<UiScope>
) : ComponentActivity(), ForegroundActivityMarker {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback {
      finish()
    }

    val uiScope = uiScopeFactory(this)

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    val uiComponent = uiScope.service<UiComponent>()

    setContent {
      CompositionLocalProvider(LocalScope provides uiScope) {
        uiComponent.decorateAppUi {
          uiComponent.appUi()
        }
      }
    }
  }
}

@Provide @Service<UiScope> data class UiComponent(
  val appUi: AppUi,
  val decorateAppUi: DecorateAppUi
)
