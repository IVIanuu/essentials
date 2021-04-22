package com.ivianuu.essentials.ui.animation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

@Stable
class AnimationElement(val key: Any) {
    internal val modifiers = mutableStateListOf<State<Modifier>>()
    internal val refs = mutableSetOf<Any>()
    private val props = mutableStateMapOf<AnimationElementPropKey<*>, Any?>()
    internal operator fun <T> set(key: AnimationElementPropKey<T>, value: T) {
        props[key] = value
    }
    operator fun <T> get(key: AnimationElementPropKey<T>): T? = props[key] as? T
}

object ContentAnimationElementKey

class AnimationElementPropKey<T>

@Stable
class AnimationElementStore {
    private val elements = mutableMapOf<Any, AnimationElement>()
    fun referenceElement(elementKey: Any, refKey: Any): AnimationElement {
        val element = elements.getOrPut(elementKey) { AnimationElement(elementKey) }
        element.refs += refKey
        return element
    }
    fun disposeRef(elementKey: Any, refKey: Any) {
        val element = elements[elementKey] ?: return
        element.refs -= refKey
        if (element.refs.isEmpty()) elements -= elementKey
    }
}

@Composable
fun animationElementFor(key: Any): AnimationElement {
    val stackChild = LocalAnimatedStackChild.current
    val refKey = remember { Any() }
    val element = stackChild.elementStore.referenceElement(key, refKey)
    DisposableEffect(stackChild) {
        onDispose { stackChild.elementStore.disposeRef(key, refKey) }
    }
    return element
}

fun Modifier.animationElement(
    key: Any,
    vararg props: Pair<AnimationElementPropKey<*>, Any?>
): Modifier = composed {
    val element = animationElementFor(key)
    props.forEach { element[it.first.cast<AnimationElementPropKey<Any?>>()] = it.second }
    element.modifiers.toSet().fold(Modifier as Modifier) { acc, modifier ->
        acc.then(modifier.value)
    }
}
