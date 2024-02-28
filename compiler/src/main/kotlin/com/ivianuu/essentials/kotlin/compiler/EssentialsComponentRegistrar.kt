/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalCompilerApi::class)

package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.*

@AutoService(ComponentRegistrar::class)
class EssentialsComponentRegistrar : ComponentRegistrar {
  override val supportsK2: Boolean
    get() = true

  override fun registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
  }
}
