/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.tuples.Tuple10
import com.ivianuu.essentials.tuples.Tuple11
import com.ivianuu.essentials.tuples.Tuple12
import com.ivianuu.essentials.tuples.Tuple13
import com.ivianuu.essentials.tuples.Tuple14
import com.ivianuu.essentials.tuples.Tuple15
import com.ivianuu.essentials.tuples.Tuple16
import com.ivianuu.essentials.tuples.Tuple17
import com.ivianuu.essentials.tuples.Tuple18
import com.ivianuu.essentials.tuples.Tuple19
import com.ivianuu.essentials.tuples.Tuple2
import com.ivianuu.essentials.tuples.Tuple20
import com.ivianuu.essentials.tuples.Tuple21
import com.ivianuu.essentials.tuples.Tuple3
import com.ivianuu.essentials.tuples.Tuple4
import com.ivianuu.essentials.tuples.Tuple5
import com.ivianuu.essentials.tuples.Tuple6
import com.ivianuu.essentials.tuples.Tuple7
import com.ivianuu.essentials.tuples.Tuple8
import com.ivianuu.essentials.tuples.Tuple9
import com.ivianuu.essentials.tuples.tupleOf
import com.ivianuu.injekt.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <A, B> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple2<A, B> {
  val result = par<Any?>(blockA, blockB, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(result[0] as A, result[1] as B)
}

suspend fun <A, B, C> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple3<A, B, C> {
  val result = par<Any?>(blockA, blockB, blockC, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(result[0] as A, result[1] as B, result[2] as C)
}

suspend fun <A, B, C, D> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple4<A, B, C, D> {
  val result = par<Any?>(blockA, blockB, blockC, blockD, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(result[0] as A, result[1] as B, result[2] as C, result[3] as D)
}

suspend fun <A, B, C, D, E> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple5<A, B, C, D, E> {
  val result = par<Any?>(blockA, blockB, blockC, blockD, blockE, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(result[0] as A, result[1] as B, result[2] as C, result[3] as D, result[4] as E)
}

suspend fun <A, B, C, D, E, F> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple6<A, B, C, D, E, F> {
  val result = par<Any?>(blockA, blockB, blockC, blockD, blockE, blockF, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F
  )
}

suspend fun <A, B, C, D, E, F, G> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple7<A, B, C, D, E, F, G> {
  val result = par<Any?>(blockA, blockB, blockC, blockD, blockE, blockF, blockG, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G
  )
}

suspend fun <A, B, C, D, E, F, G, H> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple8<A, B, C, D, E, F, G, H> {
  val result =
    par<Any?>(blockA, blockB, blockC, blockD, blockE, blockF, blockG, blockH, context = context)
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H
  )
}

suspend fun <A, B, C, D, E, F, G, H, I> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple9<A, B, C, D, E, F, G, H, I> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple10<A, B, C, D, E, F, G, H, I, J> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple11<A, B, C, D, E, F, G, H, I, J, K> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  blockQ: suspend () -> Q,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    blockQ,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P,
    result[16] as Q
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  blockQ: suspend () -> Q,
  blockR: suspend () -> R,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    blockQ,
    blockR,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P,
    result[16] as Q,
    result[17] as R
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  blockQ: suspend () -> Q,
  blockR: suspend () -> R,
  blockS: suspend () -> S,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    blockQ,
    blockR,
    blockS,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P,
    result[16] as Q,
    result[17] as R,
    result[18] as S
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  blockQ: suspend () -> Q,
  blockR: suspend () -> R,
  blockS: suspend () -> S,
  blockT: suspend () -> T,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    blockQ,
    blockR,
    blockS,
    blockT,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P,
    result[16] as Q,
    result[17] as R,
    result[18] as S,
    result[19] as T
  )
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> parTupled(
  blockA: suspend () -> A,
  blockB: suspend () -> B,
  blockC: suspend () -> C,
  blockD: suspend () -> D,
  blockE: suspend () -> E,
  blockF: suspend () -> F,
  blockG: suspend () -> G,
  blockH: suspend () -> H,
  blockI: suspend () -> I,
  blockJ: suspend () -> J,
  blockK: suspend () -> K,
  blockL: suspend () -> L,
  blockM: suspend () -> M,
  blockN: suspend () -> N,
  blockO: suspend () -> O,
  blockP: suspend () -> P,
  blockQ: suspend () -> Q,
  blockR: suspend () -> R,
  blockS: suspend () -> S,
  blockT: suspend () -> T,
  blockU: suspend () -> U,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject concurrency: Concurrency
): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> {
  val result = par<Any?>(
    blockA,
    blockB,
    blockC,
    blockD,
    blockE,
    blockF,
    blockG,
    blockH,
    blockI,
    blockJ,
    blockK,
    blockL,
    blockM,
    blockN,
    blockO,
    blockP,
    blockQ,
    blockR,
    blockS,
    blockT,
    blockU,
    context = context
  )
  @Suppress("UNCHECKED_CAST")
  return tupleOf(
    result[0] as A,
    result[1] as B,
    result[2] as C,
    result[3] as D,
    result[4] as E,
    result[5] as F,
    result[6] as G,
    result[7] as H,
    result[8] as I,
    result[9] as J,
    result[10] as K,
    result[11] as L,
    result[12] as M,
    result[13] as N,
    result[14] as O,
    result[15] as P,
    result[16] as Q,
    result[17] as R,
    result[18] as S,
    result[19] as T,
    result[20] as U
  )
}

