/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tuples

data class Tuple1<out A>(val a: A)

inline fun <A> tupleOf(a: A): Tuple1<A> {
  return Tuple1(a)
}

data class Tuple2<out A, out B>(val a: A, val b: B)

inline fun <A, B> tupleOf(a: A, b: B): Tuple2<A, B> {
  return Tuple2(a, b)
}

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C)

inline fun <A, B, C> tupleOf(a: A, b: B, c: C): Tuple3<A, B, C> {
  return Tuple3(a, b, c)
}

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)

inline fun <A, B, C, D> tupleOf(a: A, b: B, c: C, d: D): Tuple4<A, B, C, D> {
  return Tuple4(a, b, c, d)
}

data class Tuple5<out A, out B, out C, out D, out E>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E
)

inline fun <A, B, C, D, E> tupleOf(a: A, b: B, c: C, d: D, e: E): Tuple5<A, B, C, D, E> {
  return Tuple5(a, b, c, d, e)
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F
)

inline fun <A, B, C, D, E, F> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F
): Tuple6<A, B, C, D, E, F> {
  return Tuple6(a, b, c, d, e, f)
}

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G
)

inline fun <A, B, C, D, E, F, G> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G
): Tuple7<A, B, C, D, E, F, G> {
  return Tuple7(a, b, c, d, e, f, g)
}

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H
)

inline fun <A, B, C, D, E, F, G, H> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H
): Tuple8<A, B, C, D, E, F, G, H> {
  return Tuple8(a, b, c, d, e, f, g, h)
}

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I
)

inline fun <A, B, C, D, E, F, G, H, I> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I
): Tuple9<A, B, C, D, E, F, G, H, I> {
  return Tuple9(a, b, c, d, e, f, g, h, i)
}

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J
)

inline fun <A, B, C, D, E, F, G, H, I, J> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J
): Tuple10<A, B, C, D, E, F, G, H, I, J> {
  return Tuple10(a, b, c, d, e, f, g, h, i, j)
}

data class Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K
)

inline fun <A, B, C, D, E, F, G, H, I, J, K> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K
): Tuple11<A, B, C, D, E, F, G, H, I, J, K> {
  return Tuple11(a, b, c, d, e, f, g, h, i, j, k)
}

data class Tuple12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L
): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> {
  return Tuple12(a, b, c, d, e, f, g, h, i, j, k, l)
}

data class Tuple13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M
): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> {
  return Tuple13(a, b, c, d, e, f, g, h, i, j, k, l, m)
}

data class Tuple14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N
): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
  return Tuple14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
}

data class Tuple15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O
): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> {
  return Tuple15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
}

data class Tuple16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P
): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
  return Tuple16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
}

data class Tuple17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P,
  val q: Q
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P,
  q: Q
): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {
  return Tuple17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
}

data class Tuple18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P,
  val q: Q,
  val r: R
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P,
  q: Q,
  r: R
): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {
  return Tuple18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
}

data class Tuple19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P,
  val q: Q,
  val r: R,
  val s: S
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P,
  q: Q,
  r: R,
  s: S
): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> {
  return Tuple19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
}

data class Tuple20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P,
  val q: Q,
  val r: R,
  val s: S,
  val t: T
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P,
  q: Q,
  r: R,
  s: S,
  t: T
): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> {
  return Tuple20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
}

data class Tuple21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(
  val a: A,
  val b: B,
  val c: C,
  val d: D,
  val e: E,
  val f: F,
  val g: G,
  val h: H,
  val i: I,
  val j: J,
  val k: K,
  val l: L,
  val m: M,
  val n: N,
  val o: O,
  val p: P,
  val q: Q,
  val r: R,
  val s: S,
  val t: T,
  val u: U
)

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> tupleOf(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  f: F,
  g: G,
  h: H,
  i: I,
  j: J,
  k: K,
  l: L,
  m: M,
  n: N,
  o: O,
  p: P,
  q: Q,
  r: R,
  s: S,
  t: T,
  u: U
): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> {
  return Tuple21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
}

