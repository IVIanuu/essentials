package com.ivianuu.essentials.ui.compose.navigation

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.Ambient
import androidx.compose.Recompose
import com.ivianuu.compose.ActivityRefAmbient
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.Ref
import com.ivianuu.compose.TransitionHintsAmbient
import com.ivianuu.compose.ambient
import com.ivianuu.compose.sourceLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val NavigatorAmbient = Ambient.of<Navigator>()

val ComponentComposition.navigator get() = ambient(NavigatorAmbient)

interface Route {

    val key: Any

    val isFloating: Boolean

    fun ComponentComposition.compose()

    fun _compose(ComponentComposition: ComponentComposition) {
        with(ComponentComposition) {
            compose()
        }
    }

}

inline fun Route(
    isFloating: Boolean = false,
    noinline compose: ComponentComposition.() -> Unit
) = Route(sourceLocation(), isFloating, compose)

fun Route(
    key: Any,
    isFloating: Boolean = false,
    content: ComponentComposition.() -> Unit
) = object : Route {

    override val key: Any
        get() = key

    override val isFloating: Boolean
        get() = isFloating

    override fun ComponentComposition.compose() {
        content.invoke(this)
    }
}

class Navigator(
    private val activity: Ref<ComponentActivity?>,
    private val startRoute: Route
) {

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            pop()
        }
    }

    val backStack: List<Route> get() = _backStack
    private val _backStack = mutableListOf<Route>()
    lateinit var recompose: () -> Unit

    private var wasPush = true

    private val resultsByRoute = mutableMapOf<Route, CompletableDeferred<Any?>>()

    init {
        activity.onUpdate { _, newValue ->
            newValue?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        }
        _backStack.add(startRoute)
    }

    fun push(route: Route) {
        GlobalScope.launch { push<Any?>(route) }
    }

    suspend fun <T> push(route: Route): T? {
        _backStack.add(route)
        wasPush = true
        recompose()
        val deferredResult = CompletableDeferred<Any?>()
        resultsByRoute[route] = deferredResult
        return deferredResult.await() as? T
    }

    fun pop(result: Any? = null) {
        if (_backStack.size > 1) {
            val route = _backStack.removeAt(_backStack.lastIndex)
            val deferredResult = resultsByRoute.remove(route)
            deferredResult?.complete(result)
            wasPush = false
            recompose()
        } else {
            activity()?.finish()
        }
    }

    fun content(ComponentComposition: ComponentComposition) = Recompose { recompose ->
        this@Navigator.recompose = recompose

        val visibleRoutes = mutableListOf<Route>()

        for (route in _backStack.reversed()) {
            visibleRoutes.add(route)
            if (!route.isFloating) break
        }

        visibleRoutes.reversed()
            .also { println("compose routes ${it.map { it.key }}") }
            .forEach {
                ComponentComposition.group(it.key) {
                    TransitionHintsAmbient.Provider(wasPush) {
                        it._compose(ComponentComposition)
                    }
                }
            }
    }

}

fun ComponentComposition.Navigator(startRoute: ComponentComposition.() -> Route) {
    val activity = ambient(ActivityRefAmbient)
    val navigator = Navigator(activity as Ref<ComponentActivity?>, startRoute())
    NavigatorAmbient.Provider(navigator) {
        navigator.content(this)
    }
}