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

import com.ivianuu.ktuples.Nonuple
import com.ivianuu.ktuples.Octuple
import com.ivianuu.ktuples.Quadruple
import com.ivianuu.ktuples.Quintuple
import com.ivianuu.ktuples.Septuple
import com.ivianuu.ktuples.Sextuple
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import io.reactivex.functions.Function6
import io.reactivex.functions.Function7
import io.reactivex.functions.Function8
import io.reactivex.functions.Function9
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject

fun <T : Any> BehaviorSubject<T>.requireValue() =
    value ?: throw IllegalStateException("value is null")

fun <T1, T2, T3, T4> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        Function4 { t1: T1, t2: T2, t3: T3, t4: T4 ->
            Quadruple(t1, t2, t3, t4)
        })!!

fun <T1, T2, T3, T4, T5> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        Function5 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5 ->
            Quintuple(t1, t2, t3, t4, t5)
        })!!

fun <T1, T2, T3, T4, T5, T6> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        Function6 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6 ->
            Sextuple(t1, t2, t3, t4, t5, t6)
        })!!

fun <T1, T2, T3, T4, T5, T6, T7> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        Function7 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7 ->
            Septuple(t1, t2, t3, t4, t5, t6, t7)
        })!!

fun <T1, T2, T3, T4, T5, T6, T7, T8> Observables.combineLatest(
    source1: Observable<T1>,
    source2: Observable<T2>,
    source3: Observable<T3>,
    source4: Observable<T4>,
    source5: Observable<T5>,
    source6: Observable<T6>,
    source7: Observable<T7>,
    source8: Observable<T8>
) =
    Observable.combineLatest(
        source1,
        source2,
        source3,
        source4,
        source5,
        source6,
        source7,
        source8,
        Function8 { t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8 ->
            Octuple(t1, t2, t3, t4, t5, t6, t7, t8)
        })!!

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
) =
    Observable.combineLatest(
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
            Nonuple(t1, t2, t3, t4, t5, t6, t7, t8, t9)
        })!!