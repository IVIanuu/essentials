package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlin.reflect.*

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
      context.isContentRemoved = false
      onDispose {
        context.isContentRemoved = true
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
  val scope: Scope<ScreenScope>,
  val content: @Composable () -> Unit
) : RememberObserver {
  internal var isContentRemoved = false
  internal var isContextRemoved = false
  var isDisposed by mutableStateOf(false)
    private set

  internal var savedState = mutableMapOf<Any, Map<String, List<Any?>>>()

  override fun onRemembered() {
    isContextRemoved = false
  }

  override fun onForgotten() {
    isContextRemoved = true
  }

  override fun onAbandoned() {
    isContextRemoved = true
  }

  fun disposeIfNeeded() {
    if (isDisposed || !isContentRemoved || !isContextRemoved) return
    isDisposed = true
    scope.dispose()
  }
}

@Composable fun <S : Screen<*>> rememberScreenContext(
  screen: S,
  navigator: Navigator = LocalScope.current.navigator
) = rememberScreenContext(
  screen,
  navigator,
  LocalScope.current.service<ScreenContextComponent<RootNavGraph>>()
)

@Composable fun <N, S : Screen<*>> rememberScreenContext(
  screen: S,
  navigator: Navigator = LocalScope.current.navigator,
  component: ScreenContextComponent<N>
): ScreenContext<S> = remember {
  val scope = component.screenScopeFactory(navigator, screen)
  val ui = component.uiFactories[screen::class.cast()]?.invoke(navigator, scope.cast(), screen)
    ?: error("No ui factory found for $screen")
  val config = component.configFactories[screen::class.cast()]?.invoke(navigator, scope.cast(), screen)
    ?: error("No config found for $screen")
  val decorateScreen = component.decorateScreenFactory(navigator, scope.cast(), screen)
  ScreenContext(
    screen = screen,
    config = config,
    content = {
      decorateScreen {
        ui.Content()
      }
    },
    scope = scope
  ).unsafeCast()
}

@Provide data class ScreenContextComponent<N>(
  val uiFactories: Map<KClass<Screen<*>>, @NavGraph<N> UiFactory<Screen<*>>>,
  val configFactories: Map<KClass<Screen<*>>, @NavGraph<N> ScreenConfigFactory<Screen<*>>>,
  val screenScopeFactory: (@Service<ScreenScope> Navigator, @Service<ScreenScope> Screen<*>) -> Scope<ScreenScope>,
  val decorateScreenFactory: (Navigator, Scope<ScreenScope>, Screen<*>) -> DecorateScreen
) {
  @Provide companion object {
    @Provide fun rootService(
      factory: () -> ScreenContextComponent<RootNavGraph>
    ) = ProvidedService<UiScope, _>(ScreenContextComponent::class, factory)
  }
}
