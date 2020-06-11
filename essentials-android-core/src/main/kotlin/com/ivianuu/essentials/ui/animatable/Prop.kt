package com.ivianuu.essentials.ui.animatable

import androidx.compose.Stable

@Stable
interface Prop<T>

@Stable
class PropWithValue<T>(
    val prop: Prop<T>,
    val value: T
)

infix fun <T> Prop<T>.withValue(value: T): PropWithValue<T> =
    PropWithValue(this, value)

@Stable
class MetaProp<T> : Prop<T>
