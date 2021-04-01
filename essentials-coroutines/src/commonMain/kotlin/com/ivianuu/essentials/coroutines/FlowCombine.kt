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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <A, B> combine(flowA: Flow<A>, flowB: Flow<B>): Flow<Tuple2<A, B>> {
    return combine(flowA, flowB) { a, b -> tupleOf(a, b) }
}

fun <A, B, C> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>): Flow<Tuple3<A, B, C>> {
    return combine(flowA, flowB, flowC) { a, b, c -> tupleOf(a, b, c) }
}

fun <A, B, C, D> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>): Flow<Tuple4<A, B, C, D>> {
    return combine(flowA, flowB, flowC, flowD) { a, b, c, d -> tupleOf(a, b, c, d) }
}

fun <A, B, C, D, E> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>): Flow<Tuple5<A, B, C, D, E>> {
    return combine(flowA, flowB, flowC, flowD, flowE) { a, b, c, d, e -> tupleOf(a, b, c, d, e) }
}

fun <A, B, C, D, E, F> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>): Flow<Tuple6<A, B, C, D, E, F>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F) }
}

fun <A, B, C, D, E, F, G> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>): Flow<Tuple7<A, B, C, D, E, F, G>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G) }
}

fun <A, B, C, D, E, F, G, H> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>): Flow<Tuple8<A, B, C, D, E, F, G, H>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H) }
}

fun <A, B, C, D, E, F, G, H, I> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>): Flow<Tuple9<A, B, C, D, E, F, G, H, I>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I) }
}

fun <A, B, C, D, E, F, G, H, I, J> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>): Flow<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J) }
}

fun <A, B, C, D, E, F, G, H, I, J, K> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>): Flow<Tuple11<A, B, C, D, E, F, G, H, I, J, K>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>): Flow<Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>): Flow<Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>): Flow<Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>): Flow<Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>): Flow<Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>, flowQ: Flow<Q>): Flow<Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP, flowQ) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P, values[16] as Q) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>, flowQ: Flow<Q>, flowR: Flow<R>): Flow<Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP, flowQ, flowR) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P, values[16] as Q, values[17] as R) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>, flowQ: Flow<Q>, flowR: Flow<R>, flowS: Flow<S>): Flow<Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP, flowQ, flowR, flowS) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P, values[16] as Q, values[17] as R, values[18] as S) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>, flowQ: Flow<Q>, flowR: Flow<R>, flowS: Flow<S>, flowT: Flow<T>): Flow<Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP, flowQ, flowR, flowS, flowT) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P, values[16] as Q, values[17] as R, values[18] as S, values[19] as T) }
}

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> combine(flowA: Flow<A>, flowB: Flow<B>, flowC: Flow<C>, flowD: Flow<D>, flowE: Flow<E>, flowF: Flow<F>, flowG: Flow<G>, flowH: Flow<H>, flowI: Flow<I>, flowJ: Flow<J>, flowK: Flow<K>, flowL: Flow<L>, flowM: Flow<M>, flowN: Flow<N>, flowO: Flow<O>, flowP: Flow<P>, flowQ: Flow<Q>, flowR: Flow<R>, flowS: Flow<S>, flowT: Flow<T>, flowU: Flow<U>): Flow<Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>> {
    return combine(flowA, flowB, flowC, flowD, flowE, flowF, flowG, flowH, flowI, flowJ, flowK, flowL, flowM, flowN, flowO, flowP, flowQ, flowR, flowS, flowT, flowU) { values -> tupleOf(values[0] as A, values[1] as B, values[2] as C, values[3] as D, values[4] as E, values[5] as F, values[6] as G, values[7] as H, values[8] as I, values[9] as J, values[10] as K, values[11] as L, values[12] as M, values[13] as N, values[14] as O, values[15] as P, values[16] as Q, values[17] as R, values[18] as S, values[19] as T, values[20] as U) }
}

