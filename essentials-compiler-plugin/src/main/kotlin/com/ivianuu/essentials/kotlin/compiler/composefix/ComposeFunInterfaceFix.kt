/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler.composefix

import androidx.compose.compiler.plugins.kotlin.*
import androidx.compose.compiler.plugins.kotlin.lower.*
import com.ivianuu.injekt.compiler.*
import com.ivianuu.injekt.compiler.analysis.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.backend.jvm.ir.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.com.intellij.openapi.extensions.*
import org.jetbrains.kotlin.com.intellij.openapi.project.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.diagnostics.*
import org.jetbrains.kotlin.types.checker.*
import org.jetbrains.kotlin.utils.addToStdlib.*

fun composeFix(project: Project) {
  @Suppress("DEPRECATION")
  Extensions.getRootArea().getExtensionPoint(DiagnosticSuppressor.EP_NAME)
    .registerExtension(object : DiagnosticSuppressor {
      override fun isSuppressed(diagnostic: Diagnostic): Boolean =
        diagnostic.factory.name == "COMPOSABLE_INVOCATION"
     }, project)

  IrGenerationExtension.registerExtensionWithLoadingOrder(
    project,
    LoadingOrder.FIRST,
    object : IrGenerationExtension {
      @OptIn(ObsoleteDescriptorBasedAPI::class)
      override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.fixComposeFunInterfacesPreCompose(pluginContext)
      }
    }
  )
  IrGenerationExtension.registerExtensionWithLoadingOrder(
    project,
    LoadingOrder.LAST,
    object : IrGenerationExtension {
      @OptIn(ObsoleteDescriptorBasedAPI::class)
      override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.fixComposeFunInterfacesPostCompose(pluginContext)
      }
    }
  )
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
private fun IrModuleFragment.fixComposeFunInterfacesPreCompose(ctx: IrPluginContext) {
  if (!composeCompilerInClasspath) return

  transform(
    object : IrElementTransformerVoid() {
      override fun visitTypeOperator(expression: IrTypeOperatorCall): IrExpression {
        if (expression.operator == IrTypeOperator.SAM_CONVERSION &&
          expression.type.isComposableFunInterface(ctx) &&
          expression.argument.type.isComposableFunInterface(ctx))
          return expression.argument

        if (expression.operator == IrTypeOperator.SAM_CONVERSION &&
          expression.argument is IrFunctionExpression &&
          expression.type.isComposableFunInterface(ctx)) {
          val functionExpression = expression.argument as IrFunctionExpression
          val declaration = functionExpression.function
          if (!declaration.hasComposableAnnotation()) {
            declaration.annotations += DeclarationIrBuilder(ctx, declaration.symbol)
              .irCallConstructor(
                ctx.referenceConstructors(ComposableFqName)
                  .single(),
                emptyList()
              )
          }
          declaration.overriddenSymbols = listOf(
            expression.type.classOrNull!!
              .owner
              .getSingleAbstractMethod()!!
              .symbol
              .also {
                if (!it.owner.hasComposableAnnotation()) {
                  it.owner.annotations += DeclarationIrBuilder(ctx, it.owner.symbol)
                    .irCallConstructor(
                      ctx.referenceConstructors(ComposableFqName)
                        .single(),
                      emptyList()
                    )
                }
              }
          )
        }

        return super.visitTypeOperator(expression)
      }

      override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.isFakeOverride &&
          !declaration.isFakeOverriddenFromAny() &&
          declaration.parentAsClass.defaultType.isComposableFunInterface(ctx)) {
          if (!declaration.hasComposableAnnotation()) {
            declaration.annotations += DeclarationIrBuilder(ctx, declaration.symbol)
              .irCallConstructor(
                ctx.referenceConstructors(ComposableFqName)
                  .single(),
                emptyList()
              )
          }
          if (declaration.parentAsClass.defaultType.isComposableFunInterface(ctx)) {
            (declaration as IrSimpleFunction).overriddenSymbols = listOf(
              irBuiltins.functionN(
                declaration.valueParameters.size +
                    changedParamCount(declaration.valueParameters.size, 1) +
                    1
              )
                .functions
                .first { it.name.asString() == "invoke" }
                .symbol
            )
          }
        }
        return super.visitFunction(declaration)
      }

      override fun visitCall(expression: IrCall): IrExpression {
        val result = super.visitCall(expression) as IrCall
        val dispatchReceiverType = result.dispatchReceiver?.type
          ?: return result
        if (dispatchReceiverType.isComposableFunInterface(ctx) &&
          !dispatchReceiverType.hasComposableAnnotation()) {
          (dispatchReceiverType.annotations as ArrayList<IrConstructorCall>)
            .add(
              DeclarationIrBuilder(ctx, result.symbol)
                .irCallConstructor(
                  ctx.referenceConstructors(ComposableFqName)
                    .single(),
                  emptyList()
                )
            )
        }

        return result
      }
    },
    null
  )
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
private fun IrModuleFragment.fixComposeFunInterfacesPostCompose(ctx: IrPluginContext) {
  if (!composeCompilerInClasspath) return

  transform(
    object : IrElementTransformerVoid() {
      override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.hasComposableAnnotation()) {
          declaration.annotations = declaration.annotations
            .flatMap {
              buildList {
                if (it.type.classifierOrFail.descriptor.fqNameSafe != ComposableFqName ||
                  none { it.type.classifierOrFail.descriptor.fqNameSafe == ComposableFqName })
                  add(it)
              }
            }

          if (declaration.parentClassOrNull?.defaultType?.superTypes()
              ?.any { it.isComposableFunInterface(ctx) } == true) {
            (declaration as IrSimpleFunction).overriddenSymbols = listOf(
              irBuiltins.functionN(declaration.valueParameters.size)
                .functions.first { it.name.asString() == "invoke" }.symbol
            )
          }
        }
        return super.visitFunction(declaration)
      }
    },
    null
  )
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
private fun IrType.isComposableFunInterface(ctx: IrPluginContext): Boolean {
  val classifier = classifierOrNull?.descriptor ?: return false
  return classifier.safeAs<ClassDescriptor>()?.isFun == true &&
      classifier.defaultType.anySuperTypeConstructor {
        it.declarationDescriptor?.hasComposableAnnotation() == true
      }
}

private fun IrGenerationExtension.Companion.registerExtensionWithLoadingOrder(
  project: Project,
  loadingOrder: LoadingOrder,
  extension: IrGenerationExtension,
) {
  project.extensionArea
    .getExtensionPoint(IrGenerationExtension.extensionPointName)
    .registerExtension(extension, loadingOrder, project)
}

private val ComposableFqName = FqName("androidx.compose.runtime.Composable")

private val composeCompilerInClasspath = try {
  Class.forName("androidx.compose.compiler.plugins.kotlin.analysis.ComposeWritableSlices")
  true
} catch (e: ClassNotFoundException) {
  false
}
