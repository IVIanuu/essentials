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

package com.ivianuu.ktuples

import java.io.Serializable

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