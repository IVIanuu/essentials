package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.Service
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.ui.UiScope
import com.ivianuu.essentials.unsafeCast
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

@Composable fun <S : Screen<*>> ScreenContent(screen: S) {
  ScreenContent(rememberScreenContext(screen))
}

@Composable fun <S : Screen<*>> ScreenContent(context: ScreenContext<S>) {
  if (context.isDisposed) return

  val compositionKey = currentCompositeKeyHash

  val savableStateRegistry = remember {
    SaveableStateRegistry(
      restoredValues = context.savedState.remove(compositionKey),
      canBeSaved = { true }
    )
  }

  CompositionLocalProvider(
    LocalScope provides context.scope,
    LocalSaveableStateRegistry provides savableStateRegistry
  ) {
    context.content()

    DisposableEffect(true) {
      context.isContentRemoved = true
      onDispose {
        context.isContentRemoved = false
        if (!context.isContextRemoved)
          context.savedState[compositionKey] = savableStateRegistry.performSave()
        context.disposeIfNeeded()
      }
    }
  }
}

@Stable class ScreenContext<S : Screen<*>>(
  val screen: S,
  val config: ScreenConfig<S>? = null,
  val scope: Scope<ScreenScope<S>>,
  internal val content: @Composable () -> Unit
) {
  internal var isContentRemoved = false
  internal var isContextRemoved = false
  var isDisposed by mutableStateOf(false)
    private set

  internal var savedState = mutableMapOf<Any, Map<String, List<Any?>>>()

  internal fun disposeIfNeeded() {
    if (isDisposed) return
    if (isContentRemoved || !isContextRemoved) return
    isDisposed = true
    scope.dispose()
  }
}

@Composable fun <S : Screen<*>> rememberScreenContext(
  screen: S,
  navigator: Navigator = LocalScope.current.navigator,
  componentFactory: @Composable () -> ScreenContextComponent = {
    val scope = LocalScope.current
    remember(scope) { scope.service() }
  }
): ScreenContext<S> {
  val component = componentFactory()

  var currentModel by remember { mutableStateOf<Any?>(null) }

  val (model, context) = remember {
    val scope = component.screenScopeFactories[screen::class.cast()]
      ?.unsafeCast<ScreenScopeFactory<S>>()
      ?.invoke(navigator, screen)
    checkNotNull(scope) { "No scope factory found for $screen" }
    val ui = component.uiFactories[screen::class.cast()]?.invoke(navigator, scope.cast(), screen)
    checkNotNull(ui) { "No ui factory found for $screen" }
    val config = component.configFactories[screen::class.cast()]?.invoke(navigator, scope.cast(), screen)
    val model = component.modelFactories[screen::class.cast()]?.invoke(navigator, scope.cast(), screen)
    checkNotNull(model) { "No model found for $screen" }
    val decorateScreen = component.decorateScreenFactory(navigator, scope.cast(), screen)
    model to ScreenContext<S>(
      screen = screen,
      config = config.cast(),
      content = {
        decorateScreen {
          with(ui as Ui<S, Any>) {
            with(currentModel as Any) {
              invoke(this)
            }
          }
        }
      },
      scope = scope
    )
  }

  ObserveScope(
    remember {
      {
        currentModel = model()

        DisposableEffect(true) {
          onDispose {
            context.isContextRemoved = true
            context.disposeIfNeeded()
          }
        }
      }
    }
  )

  return context
}

@Composable private fun ObserveScope(body: @Composable () -> Unit) {
  body()
}

@Provide @Service<UiScope> data class ScreenContextComponent(
  val screenScopeFactories: Map<KClass<Screen<*>>, ScreenScopeFactory<Screen<*>>>,
  val uiFactories: Map<KClass<Screen<*>>, UiFactory<Screen<*>>>,
  val modelFactories: Map<KClass<Screen<*>>, ModelFactory<Screen<*>, *>>,
  val configFactories: Map<KClass<Screen<*>>, ScreenConfigFactory<Screen<*>>>,
  val decorateScreenFactory: (Navigator, Scope<ScreenScope<*>>, Screen<*>) -> DecorateScreen,
)
