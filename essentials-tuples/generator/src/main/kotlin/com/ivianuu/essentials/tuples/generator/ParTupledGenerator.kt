/*
 * Copyright 2021 Manuel Wrage
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

fun generatePar(genDir: File) {
  val file = genDir.resolve("ParTupled.kt")
  file.createNewFile()
  val code = buildString {
    appendLine("package com.ivianuu.essentials.coroutines")
    appendLine()
    appendLine("import com.ivianuu.essentials.tuples.tupleOf")
    appendLine("import com.ivianuu.injekt.*")
    appendLine("import kotlin.coroutines.*")
    (2..TUPLES_SIZE).forEach { tuple ->
      appendLine("import com.ivianuu.essentials.tuples.Tuple$tuple")
    }
    appendLine()
    (2..TUPLES_SIZE).forEach { tuple ->
      append("suspend fun <")
      (1..tuple).forEachIndexed { index, arg ->
        append("${ALPHABET[index]}")
        if (index != tuple - 1) append(", ")
      }
      append("> parTupled(")
      (1..tuple).forEachIndexed { index, arg ->
        val char = ALPHABET[index]
        append("block$char: suspend () -> $char")
        if (index != tuple - 1) append(", ")
      }
      append(", context: CoroutineContext = EmptyCoroutineContext")
      append(", @Inject concurrency: Concurrency): Tuple$tuple<")
      (1..tuple).forEachIndexed { index, arg ->
        append("${ALPHABET[index]}")
        if (index != tuple - 1) append(", ")
      }
      appendLine("> {")
      append("    val result = par<Any?>(")
      (1..tuple).forEachIndexed { index, arg ->
        append("block${ALPHABET[index]}")
        if (index != tuple - 1) append(", ")
      }
      append(", context = context")
      appendLine(")")
      appendLine("    @Suppress(\"UNCHECKED_CAST\")")
      append("    return tupleOf(")
      (1..tuple).forEachIndexed { index, arg ->
        append("result[$index] as ${ALPHABET[index]}")
        if (index != tuple - 1) append(", ")
      }
      appendLine(")")
      appendLine("}")

      appendLine()
    }
  }
  file.writeText(code)
}
