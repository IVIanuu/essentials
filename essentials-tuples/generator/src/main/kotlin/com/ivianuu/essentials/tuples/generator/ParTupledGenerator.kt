/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tuples.generator

import java.io.File

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
