/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.app

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.lifecycle.*
import essentials.*
import essentials.ui.app.*
import injekt.*
import kotlinx.coroutines.*

@Provide @AndroidComponent class EsActivity(
  private val uiScopeFactory: (@Service<UiScope> ComponentActivity) -> @New Scope<UiScope>
) : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback { finish() }

    val uiScope = uiScopeFactory(this)
    lifecycleScope.coroutineContext.job.invokeOnCompletion { uiScope.dispose() }

    val esActivityComponent = uiScope.service<EsActivityComponent>()

    setContent {
      DisposableEffect(isSystemInDarkTheme()) {
        enableEdgeToEdge()
        onDispose {  }
      }

      CompositionLocalProvider(
        LocalScope provides uiScope,
        LocalSaveableStateRegistry provides SaveableStateRegistry(emptyMap()) { true }
      ) {
        esActivityComponent.decorateAppUi {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
          ) {
            esActivityComponent.appUi()
          }
        }
      }
    }
  }
}

@Provide @Service<UiScope> data class EsActivityComponent(
  val appUi: @Composable () -> AppUi,
  val decorateAppUi: @Composable (@Composable () -> Unit) -> DecoratedAppUi
)

val Scope<*>.activity: ComponentActivity get() = service()
