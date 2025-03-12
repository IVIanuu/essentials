/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.*
import androidx.compose.ui.util.fastFold
import essentials.*
import essentials.logging.*
import injekt.*
import kotlin.reflect.*

@Stable interface Screen<T>

interface RootScreen : Screen<Unit>

interface OverlayScreen<T> : Screen<T>

interface CriticalUserFlowScreen<T> : Screen<T>

val Scope<*>.screen: Screen<*> get() = service()

typealias ScreenTransitionSpec = AnimatedContentTransitionScope<Screen<*>>.() -> ContentTransform

data class ScreenConfig<T : Screen<*>>(
  val enterTransitionSpec: (ScreenTransitionSpec)? = null,
  val exitTransitionSpec: (ScreenTransitionSpec)? = null
) {
  constructor(transitionSpec: ScreenTransitionSpec) : this(transitionSpec, transitionSpec)
}

class ScreenScope {
  override fun toString() = "ScreenScope"
}

@Stable fun interface ScreenDecorator : ExtensionPoint<ScreenDecorator> {
  @Composable fun DecoratedContent(content: @Composable () -> Unit)
}

@Stable @Provide class DecorateScreen(
  private val records: List<ExtensionPointRecord<ScreenDecorator>>,
  private val logger: Logger
) {
  @Composable fun DecoratedContent(content: @Composable () -> Unit) {
    val combinedDecorator: @Composable (@Composable () -> Unit) -> Unit = remember(records) {
      records
        .sortedWithLoadingOrder()
        .fastFold({ it() }) { acc, record ->
          { content ->
            acc {
              logger.d { "decorate screen with ${record.key.qualifiedName}" }
              record.instance.DecoratedContent(content)
            }
          }
        }
    }

    logger.d { "decorate screen $content with combined $combinedDecorator" }

    combinedDecorator(content)
  }
}

@Provide object ScreenProviders {
  @Provide fun <@AddOn T : @UiTag<S> Unit, S : Screen<*>> rootNavGraphUiFactory(
    screenClass: KClass<S>,
    uiContent: @Composable (Navigator, Scope<ScreenScope>, S) -> @UiTag<S> Unit
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> UiContent<Screen<*>>> =
    (screenClass to uiContent).unsafeCast()

  @Provide fun <@AddOn T : @UiTag<S> Unit, S : Screen<*>> rootNavGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<RootNavGraph> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()

  @Provide fun <@AddOn T : @NavGraph<N> @UiTag<S> Unit, N, S : Screen<*>> navGraphUiFactory(
    screenClass: KClass<S>,
    uiContent: @Composable (Navigator, Scope<ScreenScope>, S) -> @NavGraph<N> @UiTag<S> Unit
  ): Pair<KClass<Screen<*>>, @NavGraph<N> UiContent<Screen<*>>> =
    (screenClass to uiContent).unsafeCast()

  @Provide fun <@AddOn T : @NavGraph<N> @UiTag<S> Unit, N, S : Screen<*>> navGraphConfigFactory(
    screenClass: KClass<S>,
    screenConfigFactory: ScreenConfigFactory<S> = { _, _, _ -> ScreenConfig() }
  ): Pair<KClass<Screen<*>>, @NavGraph<N> ScreenConfigFactory<Screen<*>>> =
    (screenClass to screenConfigFactory).unsafeCast()
}

typealias UiContent<S> = @Composable (Navigator, Scope<ScreenScope>, S) -> @UiTag<S> Unit

typealias ScreenConfigFactory<S> = (Navigator, Scope<ScreenScope>, S) -> ScreenConfig<S>
