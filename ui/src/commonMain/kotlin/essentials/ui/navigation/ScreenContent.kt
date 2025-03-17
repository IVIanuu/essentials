package essentials.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import essentials.*
import essentials.app.*
import essentials.ui.*
import injekt.*
import kotlin.reflect.*

@Composable fun <S : Screen<*>> ScreenContent(
  screen: S,
  navigator: Navigator = inject
) {
  ScreenContent(rememberScreenState(screen))
}

@Composable fun <S : Screen<*>> ScreenContent(state: ScreenState<S>) {
  if (state.isDisposed) return

  val compositionKey = currentCompositeKeyHash

  val savableStateRegistry = remember {
    SaveableStateRegistry(
      restoredValues = state.savedState.remove(compositionKey),
      canBeSaved = { true }
    )
  }

  CompositionLocalProvider(
    LocalScope provides state.scope,
    LocalSaveableStateRegistry provides savableStateRegistry
  ) {
    state.content()

    DisposableEffect(true) {
      state.isContentRemoved = false
      onDispose {
        state.isContentRemoved = true
        if (!state.isStateRemoved)
          state.savedState[compositionKey] = savableStateRegistry.performSave()
        state.disposeIfNeeded()
      }
    }
  }
}

@Stable class ScreenState<S : Screen<*>>(
  val screen: S,
  val config: ScreenConfig<S>? = null,
  val scope: Scope<ScreenScope>,
  val content: @Composable () -> Unit
) : RememberObserver {
  internal var isContentRemoved = false
  internal var isStateRemoved = false
  var isDisposed by mutableStateOf(false)
    private set

  internal var savedState = mutableMapOf<Any, Map<String, List<Any?>>>()

  override fun onRemembered() {
    isStateRemoved = false
  }

  override fun onForgotten() {
    isStateRemoved = true
  }

  override fun onAbandoned() {
    isStateRemoved = true
  }

  fun disposeIfNeeded() {
    if (isDisposed || !isContentRemoved || !isStateRemoved) return
    isDisposed = true
    scope.dispose()
  }
}

@Composable fun <S : Screen<*>> rememberScreenState(
  screen: S,
  navigator: Navigator = inject,
  component: NavigationComponent<*> = LocalScope.current.service<NavigationComponent<RootNavGraph>>()
): ScreenState<S> = remember {
  val scope = component.screenScopeFactory(navigator, screen)
  val ui = component.uiFactories[screen::class.cast()]
    ?: error("No ui factory found for $screen")
  val config = component.configFactories[screen::class.cast()]
    ?.invoke(navigator, scope.cast(), screen)
    ?: error("No config found for $screen")
  ScreenState(
    screen = screen,
    config = config,
    content = {
      component.decorateScreen(navigator, scope, screen) {
        ui(navigator, scope.cast(), screen)
      }
    },
    scope = scope
  ).unsafeCast()
}

@Stable @Provide data class NavigationComponent<N>(
  val uiFactories: Map<KClass<Screen<*>>, @NavGraph<N> UiContent<Screen<*>>>,
  val configFactories: Map<KClass<Screen<*>>, @NavGraph<N> ScreenConfigFactory<Screen<*>>>,
  val screenScopeFactory: (@Service<ScreenScope> Navigator, @Service<ScreenScope> Screen<*>) -> @New Scope<ScreenScope>,
  val decorateScreen: @Composable (Navigator, Scope<ScreenScope>, Screen<*>, @Composable () -> Unit) -> DecoratedScreenContent
) {
  @Provide companion object {
    @Provide fun rootService(
      factory: () -> NavigationComponent<RootNavGraph>
    ) = ProvidedService<UiScope, _>(NavigationComponent::class, factory)
  }
}
