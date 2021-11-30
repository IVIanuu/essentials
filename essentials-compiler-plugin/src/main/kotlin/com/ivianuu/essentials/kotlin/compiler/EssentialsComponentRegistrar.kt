/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.AutoService
import com.ivianuu.essentials.kotlin.compiler.exhaustive.exhaustive
import com.ivianuu.essentials.kotlin.compiler.experimental.experimental
import com.ivianuu.essentials.kotlin.compiler.serializationfix.serializationFix
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(ComponentRegistrar::class)
class EssentialsComponentRegistrar : ComponentRegistrar {
  override fun registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
    exhaustive(project)
    experimental(project)
    serializationFix(project)
  }
}

fun IrGenerationExtension.Companion.registerExtensionWithLoadingOrder(
  project: Project,
  loadingOrder: LoadingOrder,
  extension: IrGenerationExtension,
) {
  project.extensionArea
    .getExtensionPoint(IrGenerationExtension.extensionPointName)
    .registerExtension(extension, loadingOrder, project)
}
