/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.*
import essentials.ui.app.*
import injekt.*
import kotlinx.coroutines.*

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
      DisposableEffect(isSystemInDarkTheme()) {
        enableEdgeToEdge()
        onDispose {  }
      }

      CompositionLocalProvider(LocalScope provides uiScope) {
        esActivityComponent.decorateAppUi.DecoratedContent {
          esActivityComponent.appUi.Content()
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
