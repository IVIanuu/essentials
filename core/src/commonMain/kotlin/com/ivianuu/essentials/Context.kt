package com.ivianuu.essentials

import com.ivianuu.essentials.Context.Empty
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject

inline fun <A, R> provide(@Provide a: A, block: (@Provide Context1<A>) -> R): R = block(inject())

inline fun <A, B, R> provide(@Provide a: A, @Provide b: B, block: (@Provide Context2<A, B>) -> R): R =
  block(inject())

inline fun <A, B, C, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  block: (@Provide Context3<A, B, C>) -> R
): R = block(inject())

inline fun <A, B, C, D, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  block: (@Provide Context4<A, B, C, D>) -> R
): R = block(inject())

inline fun <A, B, C, D, E, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  @Provide e: E,
  block: (@Provide Context5<A, B, C, D, E>) -> R
): R = block(inject())

inline fun <A, B, C, D, E, F, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  @Provide e: E,
  @Provide f: F,
  block: (@Provide Context6<A, B, C, D, E, F>) -> R
): R = block(inject())

inline fun <A, B, C, D, E, F, G, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  @Provide e: E,
  @Provide f: F,
  @Provide g: G,
  block: (@Provide Context7<A, B, C, D, E, F, G>) -> R
): R = block(inject())

inline fun <A, B, C, D, E, F, G, H, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  @Provide e: E,
  @Provide f: F,
  @Provide g: G,
  @Provide h: H,
  block: (@Provide Context8<A, B, C, D, E, F, G, H>) -> R
): R = block(inject())

inline fun <A, B, C, D, E, F, G, H, I, R> provide(
  @Provide a: A,
  @Provide b: B,
  @Provide c: C,
  @Provide d: D,
  @Provide e: E,
  @Provide f: F,
  @Provide g: G,
  @Provide h: H,
  @Provide i: I,
  block: (@Provide Context9<A, B, C, D, E, F, G, H, I>) -> R
): R = block(inject())

typealias Context1<A> = Context<A, Empty, Empty, Empty, Empty, Empty, Empty, Empty, Empty>
typealias Context2<A, B> = Context<A, B, Empty, Empty, Empty, Empty, Empty, Empty, Empty>
typealias Context3<A, B, C> = Context<A, B, C, Empty, Empty, Empty, Empty, Empty, Empty>
typealias Context4<A, B, C, D> = Context<A, B, C, D, Empty, Empty, Empty, Empty, Empty>
typealias Context5<A, B, C, D, E> = Context<A, B, C, D, E, Empty, Empty, Empty, Empty>
typealias Context6<A, B, C, D, E, F> = Context<A, B, C, D, E, F, Empty, Empty, Empty>
typealias Context7<A, B, C, D, E, F, G> = Context<A, B, C, D, E, F, G, Empty, Empty>
typealias Context8<A, B, C, D, E, F, G, H> = Context<A, B, C, D, E, F, G, H, Empty>
typealias Context9<A, B, C, D, E, F, G, H, I> = Context<A, B, C, D, E, F, G, H, I>

@Provide data class Context<A, B, C, D, E, F, G, H, I>(
  @Inject @property:Provide val a: A,
  @Inject @property:Provide val b: B,
  @Inject @property:Provide val c: C,
  @Inject @property:Provide val d: D,
  @Inject @property:Provide val e: E,
  @Inject @property:Provide val f: F,
  @Inject @property:Provide val g: G,
  @Inject @property:Provide val h: H,
  @Inject @property:Provide val i: I
) {
  @Provide object Empty
}
