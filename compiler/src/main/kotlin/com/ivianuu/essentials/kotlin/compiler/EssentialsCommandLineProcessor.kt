/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class EssentialsCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String
    get() = "com.ivianuu.essentials"
  override val pluginOptions: Collection<AbstractCliOption>
    get() = emptyList()
}