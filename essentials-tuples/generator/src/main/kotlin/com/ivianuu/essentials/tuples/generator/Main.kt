/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.tuples.generator

import java.io.File

fun main() {
  val tuplesGenDir = File("essentials-tuples/src/commonMain/kotlin/com/ivianuu/essentials/tuples/")
  val coroutinesGenDir =
    File("essentials-coroutines/src/commonMain/kotlin/com/ivianuu/essentials/coroutines/")
  tuplesGenDir.mkdirs()
  generateTuples(tuplesGenDir)
  generateFlowCombine(coroutinesGenDir)
  generatePar(coroutinesGenDir)
}
