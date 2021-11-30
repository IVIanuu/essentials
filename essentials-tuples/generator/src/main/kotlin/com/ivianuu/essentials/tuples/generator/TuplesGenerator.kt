/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tuples.generator

import java.io.*

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
