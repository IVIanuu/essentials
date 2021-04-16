package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.*
import com.ivianuu.essentials.kotlin.compiler.optics.*
import com.ivianuu.essentials.kotlin.compiler.serializationfix.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.com.intellij.openapi.extensions.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.*

@AutoService(ComponentRegistrar::class)
class EssentialsComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        project.serializationFix()
        project.optics()
    }
}

fun IrGenerationExtension.Companion.registerExtensionWithLoadingOrder(
    project: MockProject,
    loadingOrder: LoadingOrder,
    extension: IrGenerationExtension,
) {
    project.extensionArea
        .getExtensionPoint(IrGenerationExtension.extensionPointName)
        .registerExtension(extension, loadingOrder, project)
}
