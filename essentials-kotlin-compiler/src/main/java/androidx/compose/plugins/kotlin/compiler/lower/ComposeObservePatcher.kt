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

package androidx.compose.plugins.kotlin.compiler.lower

import androidx.compose.plugins.kotlin.ComposableAnnotationChecker
import androidx.compose.plugins.kotlin.ComposeUtils.generateComposePackageName
import androidx.compose.plugins.kotlin.KtxNameConventions
import androidx.compose.plugins.kotlin.KtxNameConventions.UPDATE_SCOPE
import androidx.compose.plugins.kotlin.analysis.ComposeWritableSlices
import androidx.compose.plugins.kotlin.getKeyValue
import androidx.compose.plugins.kotlin.isEmitInline
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irIfThen
import org.jetbrains.kotlin.backend.common.lower.irNot
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irEqeqeq
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrFunctionImpl
import org.jetbrains.kotlin.ir.descriptors.WrappedReceiverParameterDescriptor
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrContinue
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit

class ComposeObservePatcher(val context: IrPluginContext) : IrElementTransformerVoid() {

    private val typeTranslator = context.typeTranslator
    private fun KotlinType.toIrType(): IrType = typeTranslator.translateType(this)

    private fun ResolvedCall<*>.isParameterless(): Boolean {
        return dispatchReceiver != null && extensionReceiver != null && valueArguments.isEmpty()
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        super.visitFunction(declaration)

        // Only insert observe scopes in non-empty composable function
        if (declaration.body == null)
            return declaration
        if (!isComposable(declaration))
            return declaration

        val descriptor = declaration.descriptor

        // Do not insert observe scope in an inline function
        if (descriptor.isInline)
            return declaration

        // Do not insert an observe scope in an inline composable lambda
        descriptor.findPsi()?.let { psi ->
            (psi as? KtFunctionLiteral)?.let {
                if (InlineUtil.isInlinedArgument(
                        it,
                        context.bindingContext,
                        false
                    )
                )
                    return declaration
                if (it.isEmitInline(context.bindingContext)) {
                    return declaration
                }
            }
        }

        // Do not insert an observe scope if the function has a return result
        if (descriptor.returnType.let { it == null || !it.isUnit() })
            return declaration

        // Check if the descriptor has restart scope calls resolved
        val bindingContext = context.bindingContext
        if (descriptor is SimpleFunctionDescriptor) {
            val composerResolvedCall =
                bindingContext.get(ComposeWritableSlices.RESTART_COMPOSER, descriptor)

            val oldBody = declaration.body

            if (composerResolvedCall != null &&
                oldBody != null &&
                // if getting the composer requires a parameter (like `<this>`), this code will
                // currently fail. We should fix this similar to how ComposeCallTransformer solves
                // this
                composerResolvedCall.isParameterless()
            ) {
                val composerDescriptor =
                    composerResolvedCall.resultingDescriptor as PropertyDescriptor
                val startRestartGroupDescriptor = getStartRestartGroup(composerDescriptor)
                val endRestartGroupDescriptor = getEndRestartGroup(composerDescriptor)

                if (startRestartGroupDescriptor != null && endRestartGroupDescriptor != null) {
                    val symbolTable = context.symbolTable

                    // Create call to get the composer
                    val unitType = context.irBuiltIns.unitType
                    fun composer() = irGetProperty(composerResolvedCall)

                    // Create call to startRestartGroup
                    val startRestartGroup = irMethodCall(
                        composer(),
                        startRestartGroupDescriptor
                    ).apply {
                        putValueArgument(
                            0,
                            keyExpression(
                                descriptor,
                                declaration.startOffset,
                                context.builtIns.intType.toIrType()
                            )
                        )
                    }

                    // Create call to endRestartGroup
                    val endRestartGroup = irMethodCall(composer(), endRestartGroupDescriptor)

                    val irBuilder = DeclarationIrBuilder(context, declaration.symbol)
                    val updateScopeDescriptor =
                        endRestartGroupDescriptor.returnType?.memberScope?.getContributedFunctions(
                            UPDATE_SCOPE,
                            NoLookupLocation.FROM_BACKEND
                        )?.singleOrNull()
                            ?: error("updateScope not found in result type of endRestartGroup")
                    val updateScopeArgument:
                                (outerBuilder: IrBlockBuilder) -> IrExpression = { outerBuilder ->
                        // Create self-invoke lambda
                        val blockParameterDescriptor =
                            updateScopeDescriptor.valueParameters.singleOrNull()
                                ?: error("expected a single block parameter for updateScope")
                        val blockParameterType = blockParameterDescriptor.type
                        val selfSymbol = declaration.symbol

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
                                blockParameterType,
                                Modality.FINAL,
                                Visibilities.LOCAL
                            )
                        }

                        val fn = IrFunctionImpl(
                            UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                            IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
                            IrSimpleFunctionSymbolImpl(lambdaDescriptor),
                            context.irBuiltIns.unitType
                        ).also {
                            it.parent = declaration
                            val localIrBuilder = DeclarationIrBuilder(context, it.symbol)
                            it.body = localIrBuilder.irBlockBody {
                                // Call the function again with the same parameters
                                +irReturn(irCall(selfSymbol).apply {
                                    descriptor.valueParameters.forEachIndexed {
                                            index, valueParameter ->
                                        val value = declaration.valueParameters[index].symbol
                                        putValueArgument(
                                            index, IrGetValueImpl(
                                                UNDEFINED_OFFSET,
                                                UNDEFINED_OFFSET,
                                                valueParameter.type.toIrType(),
                                                value
                                            )
                                        )
                                    }
                                    descriptor.dispatchReceiverParameter?.let {
                                            receiverDescriptor ->
                                        // Ensure we get the correct type by trying to avoid
                                        // going through a KotlinType if possible.
                                        val receiverType = (receiverDescriptor as?
                                                WrappedReceiverParameterDescriptor)?.owner?.type
                                            ?: receiverDescriptor.type.toIrType()
                                        val receiver = irGet(
                                            receiverType,
                                            declaration.dispatchReceiverParameter?.symbol
                                                ?: error(
                                                    "Expected dispatch receiver on declaration"
                                                )
                                        )

                                        // Save the dispatch receiver into a temporary created in
                                        // the outer scope because direct references to the
                                        // receiver sometimes cause an invalid name, "$<this>", to
                                        // be generated.
                                        val tmp = outerBuilder.irTemporary(
                                            value = receiver,
                                            nameHint = "rcvr",
                                            irType = receiverType
                                        )
                                        dispatchReceiver = irGet(tmp)
                                    }
                                    descriptor.extensionReceiverParameter?.let {
                                            receiverDescriptor ->
                                        extensionReceiver = irGet(
                                            receiverDescriptor.type.toIrType(),
                                            declaration.extensionReceiverParameter?.symbol
                                                ?: error(
                                                    "Expected extension receiver on declaration"
                                                )
                                        )
                                    }
                                    descriptor.typeParameters.forEachIndexed { index, descriptor ->
                                        putTypeArgument(index, descriptor.defaultType.toIrType())
                                    }
                                })
                            }
                        }
                        irBuilder.irBlock(origin = IrStatementOrigin.LAMBDA) {
                            +fn
                            +IrFunctionReferenceImpl(
                                UNDEFINED_OFFSET,
                                UNDEFINED_OFFSET,
                                blockParameterType.toIrType(),
                                fn.symbol,
                                0,
                                IrStatementOrigin.LAMBDA
                            )
                        }
                    }

                    val endRestartGroupCallBlock = irBuilder.irBlock(
                        UNDEFINED_OFFSET,
                        UNDEFINED_OFFSET
                    ) {
                        val result = irTemporary(endRestartGroup)
                        val updateScopeSymbol =
                            symbolTable.referenceSimpleFunction(updateScopeDescriptor)
                        +irIfThen(irNot(irEqeqeq(irGet(result.type, result.symbol), irNull())),
                            irCall(callee = updateScopeSymbol, type = context.irBuiltIns.unitType).apply {
                                dispatchReceiver = irGet(result.type, result.symbol)
                                putValueArgument(
                                    0,
                                    updateScopeArgument(this@irBlock)
                                )
                            }
                        )
                    }

                    when (oldBody) {
                        is IrBlockBody -> {
                            val earlyReturn = findPotentialEarly(oldBody)
                            if (earlyReturn != null) {
                                if (earlyReturn is IrReturn &&
                                    oldBody.statements.lastOrNull() == earlyReturn) {
                                    // Transform block from:
                                    // {
                                    //   ...
                                    //   return value
                                    // }
                                    // to:
                                    // {
                                    //  composer.startRestartGroup()
                                    //  ...
                                    //  val tmp = value
                                    //  composer.endRestartGroup()
                                    //  return tmp
                                    // }
                                    declaration.body = irBuilder.irBlockBody {
                                        +startRestartGroup
                                        oldBody.statements
                                            .take(oldBody.statements.size - 1)
                                            .forEach { +it }
                                        val temp = irTemporary(earlyReturn.value)
                                        +endRestartGroupCallBlock
                                        +irReturn(irGet(temp))
                                    }
                                } else {
                                    // Transform the block into
                                    // composer.startRestartGroup()
                                    // try {
                                    //   ... old statements ...
                                    // } finally {
                                    //    composer.endRestartGroup()
                                    // }
                                    declaration.body = irBuilder.irBlockBody {
                                        +IrTryImpl(
                                            oldBody.startOffset, oldBody.endOffset, unitType,
                                            IrBlockImpl(
                                                UNDEFINED_OFFSET,
                                                UNDEFINED_OFFSET,
                                                unitType
                                            ).apply {
                                                statements.add(startRestartGroup)
                                                statements.addAll(oldBody.statements)
                                            },
                                            catches = emptyList(),
                                            finallyExpression = endRestartGroupCallBlock
                                        )
                                    }
                                }
                            } else {
                                // Insert the start and end calls into the block
                                oldBody.statements.add(0, startRestartGroup)
                                oldBody.statements.add(endRestartGroupCallBlock)
                            }
                            return declaration
                        }
                        else -> {
                            error("are you sure?")
                            // Composable function do not use IrExpressionBody as they are converted
                            // by the call lowering to IrBlockBody to introduce the call temporaries.
                        }
                    }
                }
            }
        }

        // Otherwise, fallback to wrapping the code block in a call to Observe()
        val module = descriptor.module
        val observeFunctionDescriptor = module
            .getPackage(FqName(generateComposePackageName()))
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

        val irBuilder = DeclarationIrBuilder(context, declaration.symbol)

        return declaration.apply {
            body = irBuilder.irBlockBody {
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
                                if (declaration.parent == oldFunction) {
                                    declaration.parent = fn
                                }
                                return super.visitDeclaration(declaration)
                            }
                        })
                    }
                    fn.parent = declaration
                }
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

    fun irCall(descriptor: FunctionDescriptor): IrCall {
        val type = descriptor.returnType?.toIrType() ?: error("Expected a return type")
        val symbol = context.symbolTable.referenceFunction(descriptor)
        return IrCallImpl(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            type,
            symbol
        )
    }

    fun irGetProperty(resolvedCall: ResolvedCall<*>): IrCall {
        val descriptor = (resolvedCall.resultingDescriptor as? PropertyDescriptor)?.getter
            ?: error("Expected property")
        return irCall(descriptor)
    }

    fun irMethodCall(target: IrExpression, resolvedCall: ResolvedCall<*>): IrCall {
        val descriptor = (resolvedCall.resultingDescriptor as?
                FunctionDescriptor) ?: error("Expected function descriptor")
        return irMethodCall(target, descriptor)
    }

    fun irMethodCall(target: IrExpression, descriptor: FunctionDescriptor): IrCall {
        return irCall(descriptor).apply {
            dispatchReceiver = target
        }
    }

    fun MemberScope.firstFunctionOrNull(name: Name) =
        getContributedFunctions(name, NoLookupLocation.FROM_BACKEND).firstOrNull()

    fun getStartRestartGroup(descriptor: PropertyDescriptor): FunctionDescriptor? =
        descriptor
            .type
            .memberScope
            .firstFunctionOrNull(KtxNameConventions.STARTRESTARTGROUP)

    fun getEndRestartGroup(descriptor: PropertyDescriptor): FunctionDescriptor? =
        descriptor
            .type
            .memberScope
            .firstFunctionOrNull(KtxNameConventions.ENDRESTARTGROUP)

    private fun isComposable(declaration: IrFunction): Boolean {
        val tmpTrace =
            DelegatingBindingTrace(
                context.bindingContext, "tmp for composable analysis"
            )
        val composability =
            ComposableAnnotationChecker()
                .analyze(
                tmpTrace,
                declaration.descriptor
            )
        return when (composability) {
            ComposableAnnotationChecker.Composability.NOT_COMPOSABLE -> false
            ComposableAnnotationChecker.Composability.MARKED -> true
            ComposableAnnotationChecker.Composability.INFERRED -> true
        }
    }

    fun keyExpression(
        descriptor: CallableMemberDescriptor,
        sourceOffset: Int,
        intType: IrType
    ): IrExpression {
        val sourceKey = getKeyValue(descriptor, sourceOffset)
        return IrConstImpl.int(
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
            intType,
            sourceKey
        )
    }

}

private fun findPotentialEarly(block: IrBlockBody): IrExpression? {
    var result: IrExpression? = null
    block.accept(object : IrElementVisitor<Unit, Unit> {
        override fun visitElement(element: IrElement, data: Unit) {
            if (result == null)
                element.acceptChildren(this, Unit)
        }

        override fun visitBreak(jump: IrBreak, data: Unit) {
            result = jump
        }

        override fun visitContinue(jump: IrContinue, data: Unit) {
            result = jump
        }

        override fun visitReturn(expression: IrReturn, data: Unit) {
            result = expression
        }

        override fun visitDeclaration(declaration: IrDeclaration, data: Unit) {
            // Skip bodies of declarations
        }
    }, Unit)
    return result
}
