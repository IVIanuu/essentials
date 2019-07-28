package com.ivianuu.essentials.ui.compose.core

import android.content.Context
import android.content.res.Resources
import androidx.compose.Ambient
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.ui.core.Density
import androidx.ui.core.DensityReceiver
import androidx.ui.core.DensityReceiverImpl

fun <T, R> withAmbient(ambient: Ambient<T>, block: T.() -> R) = effectOf<R> {
    block(+ambient(ambient))
}

val ContextAmbient = Ambient.of<Context>("Context")

fun <R> withContext(block: Context.() -> R) = withAmbient(ContextAmbient, block)

val DensityAmbient = Ambient.of<Density>("Density")

fun <R> withDensity(block: DensityReceiver.() -> R) = effectOf<R> {
    block(DensityReceiverImpl(+ambient(DensityAmbient)))
}

val ResourcesAmbient = Ambient.of<Resources>("Resources")

fun <R> withResources(block: Resources.() -> R) = withAmbient(ResourcesAmbient, block)