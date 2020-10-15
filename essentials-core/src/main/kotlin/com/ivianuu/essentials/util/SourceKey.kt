/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.util

fun _sourceKey(): Any = error("Intrinsic")

inline fun sourceKeyOf(): Any = _sourceKey()

inline fun sourceKeyOf(p1: Any?): Any = JoinedKey(sourceKeyOf(), p1)

inline fun sourceKeyOf(p1: Any?, p2: Any?): Any =
    JoinedKey(JoinedKey(sourceKeyOf(), p1), p2)

inline fun sourceKeyOf(vararg inputs: Any?): Any =
    inputs.fold(sourceKeyOf()) { left, right -> JoinedKey(left, right) }

@PublishedApi
internal data class JoinedKey(val left: Any?, val right: Any?)
