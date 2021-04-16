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

package com.ivianuu.essentials.tuples.generator

import java.io.*

fun generateFlowCombine(genDir: File) {
    val file = genDir.resolve("FlowCombine.kt")
    file.createNewFile()
    val code = buildString {
        appendLine("package com.ivianuu.essentials.coroutines")
        appendLine()
        appendLine("import kotlinx.coroutines.flow.Flow")
        appendLine("import kotlinx.coroutines.flow.combine")
        appendLine("import com.ivianuu.essentials.tuples.tupleOf")
        (2..TUPLES_SIZE).forEach { tuple ->
            appendLine("import com.ivianuu.essentials.tuples.Tuple$tuple")
        }
        appendLine()
        (2..TUPLES_SIZE).forEach { tuple ->
            /*fun <A, B> combine(flowA: Flow<A>, flowB: Flow<B>): Flow<Tuple2<A, B>> {
                return kotlinx.coroutines.flow.combine(flowA, flowB) { a, b -> tupleOf(a, b) }
            }*/
            append("fun <")
            (1..tuple).forEachIndexed { index, arg ->
                append("${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            append("> combine(")
            (1..tuple).forEachIndexed { index, arg ->
                val char = ALPHABET[index]
                append("flow$char: Flow<$char>")
                if (index != tuple - 1) append(", ")
            }
            append("): Flow<Tuple$tuple<")
            (1..tuple).forEachIndexed { index, arg ->
                append("${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            appendLine(">> {")
            append("    return combine(")
            (1..tuple).forEachIndexed { index, arg ->
                append("flow${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            append(") { ")
            if (tuple <= 5) {
                (1..tuple).forEachIndexed { index, arg ->
                    append(ALPHABET[index].toString().decapitalize())
                    if (index != tuple - 1) append(", ")
                }
                append(" -> tupleOf(")
                (1..tuple).forEachIndexed { index, arg ->
                    append(ALPHABET[index].toString().decapitalize())
                    if (index != tuple - 1) append(", ")
                }
            } else {
                append("values -> tupleOf(")
                (1..tuple).forEachIndexed { index, arg ->
                    append("values[$index] as ${ALPHABET[index]}")
                    if (index != tuple - 1) append(", ")
                }
            }
            appendLine(") }")
            appendLine("}")

            appendLine()
        }
    }
    file.writeText(code)
}
