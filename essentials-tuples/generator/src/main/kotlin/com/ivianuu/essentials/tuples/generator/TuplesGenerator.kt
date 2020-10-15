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

import java.io.File

fun generateTuples(genDir: File) {
    val file = genDir.resolve("Tuples.kt")
    file.createNewFile()
    val code = buildString {
        appendLine("package com.ivianuu.essentials.tuples")
        appendLine()
        (1..TUPLES_SIZE).forEach { tuple ->
            append("data class Tuple$tuple<")
            (1..tuple).forEachIndexed { index, arg ->
                append("out ${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            append(">(")
            (1..tuple).forEachIndexed { index, arg ->
                val char = ALPHABET[index]
                append("val ${char.toString().decapitalize()}: $char")
                if (index != tuple - 1) append(", ")
            }
            append(")")
            appendLine()
            appendLine()

            append("inline fun <")
            (1..tuple).forEachIndexed { index, arg ->
                append("${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            append("> tupleOf(")
            (1..tuple).forEachIndexed { index, arg ->
                val char = ALPHABET[index]
                append("${char.toString().decapitalize()}: $char")
                if (index != tuple - 1) append(", ")
            }
            append("): Tuple$tuple<")
            (1..tuple).forEachIndexed { index, arg ->
                append("${ALPHABET[index]}")
                if (index != tuple - 1) append(", ")
            }
            appendLine("> {")
            append("    return Tuple$tuple(")
            (1..tuple).forEachIndexed { index, arg ->
                append(ALPHABET[index].toString().decapitalize())
                if (index != tuple - 1) append(", ")
            }
            appendLine(")")
            appendLine("}")

            appendLine()
        }
    }
    file.writeText(code)
}

const val TUPLES_SIZE = 21
val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
