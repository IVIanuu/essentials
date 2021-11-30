/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.*
import org.jetbrains.kotlin.compiler.plugin.*

@AutoService(CommandLineProcessor::class)
class EssentialsCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String
    get() = "com.ivianuu.essentials"
  override val pluginOptions: Collection<AbstractCliOption>
    get() = emptyList()
}
