/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.util.ext

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.functions.*
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.Serializable

fun <T> observable(block: ObservableEmitter<T>.() -> Unit): Observable<T> = Observable.create(block)

fun <T> BehaviorSubject(): BehaviorSubject<T> = BehaviorSubject.create()
fun <T> BehaviorSubject(default: T): BehaviorSubject<T> = BehaviorSubject.createDefault(default)
fun <T> PublishSubject(): PublishSubject<T> = PublishSubject.create()

fun <T : Any> BehaviorSubject<T>.requireValue(): T =
    value ?: error("value is null")

fun <T1, T2, T3, T4> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>
): Observable<Tuple4<T1, T2, T3, T4>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        Function4 { t1: T1, t2: T2, t3: T3, t4: T4 ->
            Tuple4(t1, t2, t3, t4)
        })
}

fun <T1, T2, T3, T4, T5> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>
): Observable<Tuple5<T1, T2, T3, T4, T5>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        Function5 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 ->
            Tuple5(t1, t2, t3, t4, t5)
        })
}

fun <T1, T2, T3, T4, T5, T6> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>
): Observable<Tuple6<T1, T2, T3, T4, T5, T6>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        Function6 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 ->
            Tuple6(t1, t2, t3, t4, t5, t6)
        })
}

fun <T1, T2, T3, T4, T5, T6, T7> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>
): Observable<Tuple7<T1, T2, T3, T4, T5, T6, T7>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        Function7 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 ->
            Tuple7(t1, t2, t3, t4, t5, t6, t7)
        })
}

fun <T1, T2, T3, T4, T5, T6, T7, T8> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>
): Observable<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        source8,
        Function8 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 ->
            Tuple8(t1, t2, t3, t4, t5, t6, t7, t8)
        })
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>,
    source9: Observable<T9>
): Observable<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
    return Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        source8,
        source9,
        Function9 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9 ->
            Tuple9(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        })
}

data class Tuple1<out A>(
    val first: A
) : Serializable {
    override fun toString(): String = "($first)"
}

data class Tuple2<out A, out B>(
    val first: A,
    val second: B
) : Serializable {
    override fun toString(): String = "($first, $second)"
}

data class Tuple3<out A, out B, out C>(
    val first: A,
    val second: B,
    val third: C
) : Serializable {
    override fun toString(): String = "($first, $second, $third)"
}

data class Tuple4<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

data class Tuple5<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth)"
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G
) : Serializable {
    override fun toString(): String = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh)"
}

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H
) : Serializable {
    override fun toString(): String =
        "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth)"
}

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I
) : Serializable {
    override fun toString(): String =
        "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth)"
}