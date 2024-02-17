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
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.DecorateAppUi
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.ui.UiScopeOwner
import com.ivianuu.essentials.ui.app.AppUi
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@Provide @AndroidComponent class EsActivity(
  private val uiScopeFactory: (@Service<UiScope> ComponentActivity) -> Scope<UiScope>
) : ComponentActivity(), UiScopeOwner {
  override lateinit var uiScope: Scope<UiScope>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback { finish() }

    uiScope = uiScopeFactory(this)
    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel { uiScope.dispose() }
    }

    val esActivityComponent = uiScope.service<EsActivityComponent>()

    setContent {
      CompositionLocalProvider(LocalScope provides uiScope) {
        esActivityComponent.decorateAppUi {
          esActivityComponent.appUi()
        }
      }
    }
  }
}

@Provide @Service<UiScope> data class EsActivityComponent(
  val appUi: AppUi,
  val decorateAppUi: DecorateAppUi
)

val Scope<*>.activity: ComponentActivity get() = service()
