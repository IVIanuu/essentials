/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.plugins.kotlin.composable

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrFunctionImpl
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit

class ComposeObservePatcher(private val context: IrPluginContext) : IrElementTransformerVoid() {

    private val typeTranslator = context.typeTranslator
    private fun KotlinType.toIrType(): IrType = typeTranslator.translateType(this)

    override fun visitFunction(declaration: IrFunction): IrStatement {
        super.visitFunction(declaration)
        val descriptor = declaration.descriptor

        if (!descriptor.isComposableFunction()) return declaration

        // Only insert observe scopes in non-empty composable function
        if (declaration.body == null)
            return declaration

        // Do not insert observe scope in an inline function
        if (descriptor.isInline) return declaration

        // Do not insert an observe scope in an inline composable lambda
        descriptor.findPsi()?.let { psi ->
            (psi as? KtFunctionLiteral)?.let {
                if (InlineUtil.isInlinedArgument(
                        it,
                        context.bindingContext,
                        false
                    )
                ) return declaration
            }
        }

        // Do not insert an observe scope if the function has a return result
        if (descriptor.returnType.let { it == null || !it.isUnit() })
            return declaration

        // Otherwise, fallback to wrapping the code block in a call to Observe()
        val module = descriptor.module
        val observeFunctionDescriptor = module
            .getPackage(FqName("androidx.compose"))
            .memberScope
            .getContributedFunctions(
                Name.identifier("Observe"),
                NoLookupLocation.FROM_BACKEND
            ).single()

        val symbolTable = context.symbolTable

        val observeFunctionSymbol = symbolTable.referenceSimpleFunction(observeFunctionDescriptor)

        val type = observeFunctionDescriptor.valueParameters[0].type

        val lambdaDescriptor = AnonymousFunctionDescriptor(
            declaration.descriptor,
            Annotations.EMPTY,
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE,
            false
        ).apply {
            initialize(
                null,
                null,
                emptyList(),
                emptyList(),
                type,
                Modality.FINAL,
                Visibilities.LOCAL
            )
        }

        return declaration.apply {
            body = DeclarationIrBuilder(context, declaration.symbol).irBlockBody {
                val fn = IrFunctionImpl(
                    UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                    IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
                    IrSimpleFunctionSymbolImpl(lambdaDescriptor),
                    context.irBuiltIns.unitType
                ).also { fn ->
                    fn.body = declaration.body.apply {
                        // Move the target for the returns to avoid introducing a non-local return.
                        // Update declaration parent that point to the old function to the new
                        // function.
                        val oldFunction = declaration
                        this?.transformChildrenVoid(object : IrElementTransformerVoid() {
                            override fun visitReturn(expression: IrReturn): IrExpression {
                                val newExpression = if (
                                    expression.returnTargetSymbol === declaration.symbol)
                                    IrReturnImpl(
                                        startOffset = expression.startOffset,
                                        endOffset = expression.endOffset,
                                        type = expression.type,
                                        returnTargetSymbol = fn.symbol,
                                        value = expression.value
                                    )
                                else expression
                                return super.visitReturn(newExpression)
                            }

                            override fun visitDeclaration(declaration: IrDeclaration): IrStatement {
                                // todo fix
                                try {
                                    if (declaration.parent == oldFunction) {
                                        declaration.parent = fn
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                return super.visitDeclaration(declaration)
                            }
                        })
                    }
                    fn.parent = declaration
                }
                /*+irCall(
                    callee = symbolTable.referenceSimpleFunction(
                        this@ComposeObservePatcher.context.moduleDescriptor
                            .getPackage(FqName("kotlin.io"))
                            .memberScope
                            .findFirstFunction("println") {
                                it.valueParameters.size == 1 &&
                                        it.valueParameters.single().type == context.builtIns.nullableAnyType
                            }
                    ),
                    type = context.irBuiltIns.unitType
                ).apply {
                    putValueArgument(
                        0,
                        irString("invoke observe ${descriptor.fqNameSafe}")
                    )
                }*/
                +irCall(
                    observeFunctionSymbol,
                    observeFunctionDescriptor,
                    context.irBuiltIns.unitType
                ).also {
                    it.putValueArgument(
                        0,
                        irBlock(origin = IrStatementOrigin.LAMBDA) {
                            +fn
                            +IrFunctionReferenceImpl(
                                UNDEFINED_OFFSET,
                                UNDEFINED_OFFSET,
                                type.toIrType(),
                                fn.symbol,
                                0,
                                IrStatementOrigin.LAMBDA
                            )
                        }
                    )
                }
            }
        }
    }

}
