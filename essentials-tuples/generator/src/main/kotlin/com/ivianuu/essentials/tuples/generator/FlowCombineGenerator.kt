/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tuples.generator

import java.io.File

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
