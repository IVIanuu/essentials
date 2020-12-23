@file:Suppress("UNCHECKED_CAST")

package com.ivianuu.essentials

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenGroup
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.ComponentElement

private val Alias: (Any?) -> Any? = { it }
fun <T : S, S> alias(): @Given (@Given T) -> S = Alias as (T) -> S
fun <T, S : T> setElement(): @GivenSetElement (@Given S) -> T = Alias as (T) -> S

fun <N : Component.Name, T : Any> componentElementBinding(
    name: N,
    key: Component.Key<T>
): @GivenSetElement (@Given () -> T) -> ComponentElement<N> = { key to it }

class GivenGroup1<A>(@GivenGroup val a: A)
fun <A> givenGroupOf(a: A) = GivenGroup1(a)

class GivenGroup2<A, B>(@GivenGroup val a: A, @GivenGroup val b: B)
fun <A, B> givenGroupOf(a: A, b: B) = GivenGroup2(a, b)

class GivenGroup3<A, B, C>(@GivenGroup val a: A, @GivenGroup val b: B, @GivenGroup val c: C)
fun <A, B, C> givenGroupOf(a: A, b: B, c: C) = GivenGroup3(a, b, c)

class GivenGroup4<A, B, C, D>(@GivenGroup val a: A, @GivenGroup val b: B, @GivenGroup val c: C, @GivenGroup val d: D)
fun <A, B, C, D> givenGroupOf(a: A, b: B, c: C, d: D) = GivenGroup4(a, b, c, d)
