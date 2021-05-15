package com.ivianuu.essentials.kotlin.compiler.serializationfix

import com.ivianuu.essentials.kotlin.compiler.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.com.intellij.openapi.extensions.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*
import org.jetbrains.kotlin.name.*

fun MockProject.serializationFix() {
  IrGenerationExtension.registerExtensionWithLoadingOrder(
    this,
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
                .flatMap { valueParameter ->
                  val allTypes = mutableSetOf<IrType>()
                  fun collectTypes(type: IrType) {
                    allTypes += type
                    (type as? IrSimpleType)?.arguments
                      ?.mapNotNull { it.typeOrNull }
                      ?.forEach { collectTypes(it) }
                  }
                  collectTypes(valueParameter.type)
                  allTypes
                    .mapNotNull { it.classOrNull?.owner }
                }
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
