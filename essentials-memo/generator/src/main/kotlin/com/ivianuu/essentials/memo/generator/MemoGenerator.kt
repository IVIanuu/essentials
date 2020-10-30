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

package com.ivianuu.essentials.memo.generator

import java.io.File

fun generateMemoFunctions(genDir: File) {
    val file = genDir.resolve("Memo.kt")
    file.createNewFile()
    val code = buildString {
        appendLine("package com.ivianuu.essentials.memo")
        appendLine()

        (0..MAX_PARAM_SIZE).forEach { parameterCount ->
            /*
            fun <P1, P2, R> ((P1, P2) -> R).memoize(cache: MutableMap<Any, R> = hashMapOf()): (P1, P2) -> R {
    return { p1, p2 ->
        var key = p1.hashCode()
        key += 31 * key + p2.hashCode()
        cache.memo(key) {
            this(p1, p2)
        }
    }
}
             */
            append("fun ")
            append("<")
            if (parameterCount != 0) {
                (1..parameterCount).forEach { p ->
                    append("P$p")
                    append(", ")
                }
            }
            append("R> ")

            fun appendType() {
                append("(")
                (1..parameterCount).forEach { p ->
                    append("P$p")
                    if (p != parameterCount) append(", ")
                }
                append(") -> R")
            }

            append("(")
            appendType()
            append(").memoize(cache: MutableMap<Int, R> = hashMapOf()): ")
            appendType()
            appendLine(" {")
            append("    return { ")
            if (parameterCount != 0) {
                (1..parameterCount).forEach { p ->
                    append("p$p")
                    if (p != parameterCount) append(", ")
                }
            }

            appendLine(" ->")

            append("        var key = ")
            if (parameterCount == 0) {
                append("0")
            } else {
                append("p1.hashCode()")
            }
            appendLine()

            if (parameterCount > 1) {
                (2..parameterCount).forEach {
                    appendLine("        key += 31 * key + p${it}.hashCode()")
                }
            }

            appendLine("        cache.memo(key) {")
            append("            this(")
            if (parameterCount != 0) {
                (1..parameterCount).forEach { p ->
                    append("p$p")
                    if (p != parameterCount) append(", ")
                }
            }
            appendLine(")")

            appendLine("        }")

            appendLine("    }")

            appendLine("}")
        }
    }
    file.writeText(code)
}

const val MAX_PARAM_SIZE = 21

