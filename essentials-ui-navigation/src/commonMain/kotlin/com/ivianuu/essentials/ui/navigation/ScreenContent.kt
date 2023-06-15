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
import com.ivianuu.injekt.Provide
import kotlin.reflect.KClass

@Composable fun <T : Screen<*>> ScreenContent(screen: T) {
  ScreenContent(rememberScreenContext(screen))
}

@Composable fun <T : Screen<*>> ScreenContent(context: ScreenContext<T>) {
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

@Stable class ScreenContext<T : Screen<*>>(
  val screen: T,
  val config: ScreenConfig<T>? = null,
  val scope: Scope<ScreenScope>,
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

@Composable fun <T : Screen<*>> rememberScreenContext(
  screen: T,
  componentFactory: @Composable () -> ScreenContextComponent = {
    val scope = LocalScope.current
    remember(scope) { scope.service() }
  },
): ScreenContext<T> {
  val component = componentFactory()

  var currentModel by remember { mutableStateOf<Any?>(null) }

  val (model, context) = remember {
    val scope = component.screenScopeFactory(component.navigator, screen)
    val ui = component.uiFactories[screen::class.cast()]?.invoke(component.navigator, scope, screen)
    checkNotNull(ui) { "No ui factory found for $screen" }
    val config = component.configFactories[screen::class.cast()]?.invoke(component.navigator, scope, screen)
    val model = component.modelFactories[screen::class.cast()]?.invoke(component.navigator, scope, screen)
    checkNotNull(model) { "No model found for $screen" }
    val decorateScreen = component.decorateScreenFactory(component.navigator, scope, screen)
    model to ScreenContext<T>(
      screen = screen,
      config = config.cast(),
      content = {
        decorateScreen {
          with(ui as Ui<*, Any>) {
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
  val configFactories: Map<KClass<Screen<*>>, ScreenConfigFactory<Screen<*>>>,
  val uiFactories: Map<KClass<Screen<*>>, UiFactory<Screen<*>>>,
  val modelFactories: Map<KClass<Screen<*>>, ModelFactory<Screen<*>, *>>,
  val decorateScreenFactory: (Navigator, Scope<ScreenScope>, Screen<*>) -> DecorateScreen,
  val screenScopeFactory: (Navigator, @Service<ScreenScope> Screen<*>) -> Scope<ScreenScope>,
  val navigator: Navigator
)
