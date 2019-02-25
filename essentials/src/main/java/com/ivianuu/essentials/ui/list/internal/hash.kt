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

package com.ivianuu.essentials.ui.list.internal

fun Long?.hash64Bit(): Long {
    if (this == null) return 0

    var value = this
    value = value xor (value shl 21)
    value = value xor value.ushr(35)
    value = value xor (value shl 4)
    return value
}

fun String?.hash64Bit(): Long {
    if (this == null) return 0
    var result = -0x340d631b7bdddcdbL
    val len = length
    for (i in 0 until len) {
        result = result xor get(i).toLong()
        result *= 0x100000001b3L
    }
    return result
}