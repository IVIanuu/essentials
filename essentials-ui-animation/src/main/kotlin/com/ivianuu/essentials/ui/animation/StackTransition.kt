package com.ivianuu.essentials.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.ivianuu.essentials.coroutines.*
import kotlinx.coroutines.*
import kotlin.time.*

typealias StackTransition = suspend StackTransitionScope.() -> Unit

interface StackTransitionScope : CoroutineScope {
    val animationRoot: AnimationRoot
    val isPush: Boolean
    val from: AnimatedStackChild<*>?
    val to: AnimatedStackChild<*>?
    fun attachTo()
    fun detachFrom()
}

fun StackTransitionScope.overlay(overlay: @Composable () -> Unit): Job = launch {
    animationRoot.animationOverlays += overlay
    runOnCancellation { animationRoot.animationOverlays -= overlay }
}

suspend fun StackTransitionScope.animate(
    spec: AnimationSpec<Float>,
    block: Animatable<Float, AnimationVector1D>.() -> Unit
) {
    Animatable(0f).animateTo(1f, spec, block = block)
}

fun defaultAnimationSpec(
    duration: Duration = 300.milliseconds,
    delay: Duration = 0.milliseconds,
    easing: Easing = FastOutSlowInEasing
) = TweenSpec<Float>(
    durationMillis = duration.toLongMilliseconds().toInt(),
    delay = delay.toLongMilliseconds().toInt(),
    easing = easing
)

fun StackTransitionScope.fromElement(key: Any): AnimationElement? =
    element(from, key)

fun StackTransitionScope.toElement(key: Any): AnimationElement? =
    element(to, key)

private fun StackTransitionScope.element(
    child: AnimatedStackChild<*>?,
    key: Any,
): AnimationElement? {
    val element = child?.elementStore?.referenceElement(this, key)
    if (element != null) {
        launch {
            runOnCancellation {
                child.elementStore.disposeRef(this, key)
            }
        }
    }
    return element
}

fun StackTransitionScope.fromElementModifier(key: Any): MutableState<Modifier>? =
    elementModifier(from, key)

fun StackTransitionScope.toElementModifier(key: Any): MutableState<Modifier>? =
    elementModifier(to, key)

private fun StackTransitionScope.elementModifier(
    child: AnimatedStackChild<*>?,
    key: Any,
): MutableState<Modifier>? {
    if (child == null) return null
    val modifier = mutableStateOf<Modifier>(Modifier)
    val element = element(child, key)!!
    element.modifiers += modifier
    launch {
        runOnCancellation {
            element.modifiers -= modifier
        }
    }
    return modifier
}

val NoOpStackTransition: StackTransition = {
    attachTo()
    detachFrom()
}

val LocalStackTransition = staticCompositionLocalOf { NoOpStackTransition }