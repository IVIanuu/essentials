/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compiler.experimental

import com.ivianuu.essentials.compiler.*
import org.junit.*

class ExperimentalTest {
  @Test fun testDoesNotShowExperimentalError() = codegen(
    """
      fun invoke() = buildList<String> {
      }
    """
  )
}