package com.ivianuu.essentials.ui.animatable

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.Stable
import androidx.compose.State
import androidx.compose.frames.modelListOf
import androidx.compose.frames.modelMapOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.staticAmbientOf
import androidx.ui.core.DrawLayerModifier
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.Modifier
import androidx.ui.core.TransformOrigin
import androidx.ui.core.boundsInRoot
import androidx.ui.core.composed
import androidx.ui.core.onPositioned
import androidx.ui.graphics.RectangleShape
import com.ivianuu.essentials.ui.animatedstack.StatefulStack
import com.ivianuu.essentials.ui.animatedstack.StatefulStackEntry

@Composable
fun AnimatableElementsRoot(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    val state = remember { AnimatableElements() }
    Providers(AnimatableElementsAmbient provides state) {
        val allEntries = listOf(remember(children) {
            StatefulStackEntry(content = children)
        }) + state.animationOverlayEntries
        StatefulStack(
            modifier = modifier + Modifier.animatableElement(Root),
            entries = allEntries
        )
    }
}

@Composable
fun animationOverlay(children: @Composable () -> Unit) {
    val stackEntry = remember {
        StatefulStackEntry(opaque = true, content = children)
    }
    stackEntry.content = children
    val animatableElements = AnimatableElementsAmbient.current
    onPreCommit(true) {
        animatableElements.animationOverlayEntries += stackEntry
        onDispose { animatableElements.animationOverlayEntries -= stackEntry }
    }
}

val Root = Any()

val AnimatableElementsAmbient = staticAmbientOf<AnimatableElements> {
    error("No AnimatableElements found")
}

@Stable
class AnimatableElements {

    internal val animationOverlayEntries = modelListOf<StatefulStackEntry>()

    private val elements = mutableMapOf<Any, AnimatableElementState>()

    @Composable
    fun getElement(tag: Any): State<AnimatableElement> {
        val state = elements.getOrPut(tag) {
            AnimatableElementState(tag)
        }
        state.refCount()
        return state.state
    }

    private inner class AnimatableElementState(private val tag: Any) {
        val state = mutableStateOf(AnimatableElement(tag))
        private var refCount = 0

        @Composable
        fun refCount() {
            onPreCommit(true) {
                refCount++
                onDispose {
                    if (refCount == 0) elements -= tag
                }
            }
        }
    }
}

class AnimatableElementPropKey<T>

data class AnimatableElementPropWithValue<T>(
    val key: AnimatableElementPropKey<T>,
    val value: T
)

class AnimatedElementProps {
    private val backing = modelMapOf<AnimatableElementPropKey<*>, Any?>()
    internal fun update(entries: Array<out AnimatableElementPropWithValue<*>>) {
        backing.clear()
        entries.forEach { backing[it.key] = it.value }
    }

    operator fun <T> get(key: AnimatableElementPropKey<T>): T = backing.getValue(key) as T
    fun <T> getOrNull(key: AnimatableElementPropKey<T>): T? = backing[key] as? T
}

infix fun <T> AnimatableElementPropKey<T>.withValue(value: T): AnimatableElementPropWithValue<T> =
    AnimatableElementPropWithValue(this, value)

class AnimatableElement(val tag: Any) {
    val properties = AnimatedElementProps()
    var layoutCoordinates: LayoutCoordinates? by mutableStateOf(null)
        internal set
    val bounds get() = layoutCoordinates?.takeIf { it.isAttached }?.boundsInRoot
    val drawLayerModifier = MutableDrawLayerModifier()
    var animationModifier: Modifier by mutableStateOf(Modifier)
}

fun Modifier.animatableElement(
    tag: Any,
    vararg properties: AnimatableElementPropWithValue<*>
): Modifier = composed {
    val elementsRoot = AnimatableElementsAmbient.current
    val element = elementsRoot.getElement(tag).value
    element.properties.update(properties)
    onPositioned { element.layoutCoordinates = it } +
            element.drawLayerModifier +
            element.animationModifier
}

class MutableDrawLayerModifier : DrawLayerModifier {
    override var scaleX by mutableStateOf(1f)
    override var scaleY by mutableStateOf(1f)
    override var alpha by mutableStateOf(1f)
    override var translationX by mutableStateOf(0f)
    override var translationY by mutableStateOf(0f)
    override var shadowElevation by mutableStateOf(0f)
    override var rotationX by mutableStateOf(0f)
    override var rotationY by mutableStateOf(0f)
    override var rotationZ by mutableStateOf(0f)
    override var transformOrigin by mutableStateOf(TransformOrigin.Center)
    override var shape by mutableStateOf(RectangleShape)
    override var clip by mutableStateOf(false)
}
