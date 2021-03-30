package com.ivianuu.essentials.kotlin.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName

@AutoService(ComponentRegistrar::class)
class EssentialsComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        fixKotlinSerialization(project)
    }
}

private fun fixKotlinSerialization(project: MockProject) {
    IrGenerationExtension.registerExtensionWithLoadingOrder(
        project,
        LoadingOrder.FIRST,
        object : IrGenerationExtension {
            override fun generate(
                moduleFragment: IrModuleFragment,
                pluginContext: IrPluginContext
            ) {
                moduleFragment.transformChildrenVoid(
                    object : IrElementTransformerVoid() {
                        override fun visitClass(declaration: IrClass): IrStatement {
                            val result = super.visitClass(declaration)
                            if (!declaration.hasAnnotation(SerializerAnnotation)) return result
                            declaration.constructors
                                .flatMap { it.valueParameters }
                                .mapNotNull { it.type.classifierOrNull?.owner }
                                .filterIsInstance<IrClass>()
                                .filter { it.kind == ClassKind.ENUM_CLASS }
                                .flatMap { it.declarations }
                                .filterIsInstance<IrFunction>()
                                .filter {
                                    it.name.asString() == "values" &&
                                            it.valueParameters.isEmpty()
                                }
                                .forEach {
                                    it.origin = IrDeclarationOrigin.ENUM_CLASS_SPECIAL_MEMBER
                                }
                            return result
                        }
                    }
                )
            }
        }
    )
}

private val SerializerAnnotation = FqName("kotlinx.serialization.Serializable")

private fun IrGenerationExtension.Companion.registerExtensionWithLoadingOrder(
    project: MockProject,
    loadingOrder: LoadingOrder,
    extension: IrGenerationExtension,
) {
    project.extensionArea
        .getExtensionPoint(IrGenerationExtension.extensionPointName)
        .registerExtension(extension, loadingOrder, project)
}
