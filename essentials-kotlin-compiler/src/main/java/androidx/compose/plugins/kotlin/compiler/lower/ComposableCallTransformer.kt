package androidx.compose.plugins.kotlin.compiler.lower

import androidx.compose.plugins.kotlin.COMPOSABLE_CALL
import androidx.compose.plugins.kotlin.ComposableCallableDescriptor
import androidx.compose.plugins.kotlin.ComposableFunctionDescriptor
import androidx.compose.plugins.kotlin.ComposerMetadata
import androidx.compose.plugins.kotlin.KtxNameConventions
import androidx.compose.plugins.kotlin.analysis.ComposeWritableSlices.COMPOSABLE_FUNCTION_DESCRIPTOR
import androidx.compose.plugins.kotlin.analysis.ComposeWritableSlices.COMPOSABLE_PROPERTY_DESCRIPTOR
import androidx.compose.plugins.kotlin.hasPivotalAnnotation
import androidx.compose.plugins.kotlin.irTrace
import androidx.compose.plugins.kotlin.isMarkedStable
import androidx.compose.plugins.kotlin.isSpecialType
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.extractParameterNameFromFunctionTypeArgument
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.getValueParameterTypesFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irFalse
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.builders.irTrue
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrFunctionImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrTypeParameterImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrValueParameterImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.copyTypeArgumentsFrom
import org.jetbrains.kotlin.ir.expressions.getValueArgument
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionReferenceImpl
import org.jetbrains.kotlin.ir.expressions.putValueArgument
import org.jetbrains.kotlin.ir.expressions.typeParametersCount
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.ir.util.endOffset
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.util.startOffset
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

class ComposableCallTransformer(val context: IrPluginContext) : IrElementTransformerVoid() {

    private val typeTranslator = context.typeTranslator
    private val builtIns = context.irBuiltIns

    private val orFunctionDescriptor = builtIns.builtIns.booleanType.memberScope
        .findFirstFunction("or") { it is FunctionDescriptor && it.isInfix }

    private val symbolTable get() = context.symbolTable

    private fun KotlinType?.isStable(): Boolean {
        return this != null &&
                !isError &&
                !isTypeParameter() &&
                !isSpecialType &&
                (
                        KotlinBuiltIns.isPrimitiveType(this) ||
                                KotlinBuiltIns.isString(this) ||
                                isFunctionType ||
                                isEnum ||
                                isMarkedStable() ||
                                (
                                        isNullable() &&
                                                makeNotNullable().isStable()
                                        )
                        )
    }

    private val KotlinType.isEnum
        get() =
            (constructor.declarationDescriptor as? ClassDescriptor)?.kind == ClassKind.ENUM_CLASS

    private val declarationStack = mutableListOf<IrFunction>()

    override fun visitFunction(declaration: IrFunction): IrStatement {
        try {
            declarationStack.push(declaration)
            return super.visitFunction(declaration)
        } finally {
            declarationStack.pop()
        }
    }

    private fun findIrCall(expr: IrStatement): IrCall {
        return when (expr) {
            is IrCall -> expr
            is IrTypeOperatorCall -> when (expr.operator) {
                IrTypeOperator.IMPLICIT_CAST -> findIrCall(expr.argument)
                IrTypeOperator.IMPLICIT_COERCION_TO_UNIT -> findIrCall(expr.argument)
                else -> error("Unhandled IrTypeOperatorCall: ${expr.operator}")
            }
            is IrBlock -> when (expr.origin) {
                IrStatementOrigin.ARGUMENTS_REORDERING_FOR_CALL ->
                    findIrCall(expr.statements.last())
                else -> error("Unhandled IrBlock origin: ${expr.origin}")
            }
            else -> error("Unhandled IrExpression: ${expr::class.java.simpleName}")
        }
    }

    override fun visitBlock(expression: IrBlock): IrExpression {
        if (expression.origin != COMPOSABLE_CALL) {
            return super.visitBlock(expression)
        }

        val descriptor = context.irTrace[COMPOSABLE_FUNCTION_DESCRIPTOR, expression]

        if (descriptor != null) {
            val (composerCall, emitOrCall) = deconstructComposeBlock(expression)

            val transformedComposerCall = composerCall.transformChildren()
            val transformed = emitOrCall.transformChildren()

            val returnType = descriptor.returnType
            return if ((returnType == null || returnType.isUnit()) && !descriptor.isInline) {
                DeclarationIrBuilder(context, declarationStack.last().symbol).irBlock {
                    +irComposableCall(transformedComposerCall, transformed, descriptor)
                }
            } else {
                DeclarationIrBuilder(context, declarationStack.last().symbol)
                    .irComposableExpr(transformedComposerCall, transformed, descriptor)
            }
        }

        val property = context.irTrace[COMPOSABLE_PROPERTY_DESCRIPTOR, expression]

        if (property != null) {
            val (composerCall, emitOrCall) = deconstructComposeBlock(expression)

            val transformedComposerCall = composerCall.transformChildren()
            val transformed = emitOrCall.transformChildren()

            return DeclarationIrBuilder(context, declarationStack.last().symbol)
                .irComposableExpr(transformedComposerCall, transformed, property)
        }

        error(
            "Expected ComposableFunctionDescriptor or ComposableEmitDescriptor\n" +
            "Found: $descriptor"
        )
    }

    private fun deconstructComposeBlock(expression: IrBlock): Pair<IrCall, IrCall> {
        assert(expression.statements.size == 2)
        // the first statement should represent the call to get the composer
        // the second statement should represent the composable call or emit
        val (first, second) = expression.statements
        val composerCall = findIrCall(first)
        val emitOrCall = findIrCall(second)
        return composerCall to emitOrCall
    }

    private fun IrExpression.isReorderTemporaryVariable(): Boolean {
        return this is IrGetValue &&
                symbol.owner.origin == IrDeclarationOrigin.IR_TEMPORARY_VARIABLE
    }

    private fun IrExpression.unwrapReorderTemporaryVariable(): IrExpression {
        val getValue = this as IrGetValue
        val variable = getValue.symbol.owner as IrVariableImpl
        return variable.initializer!!
    }

    private fun IrBlockBuilder.irComposableCall(
        composerCall: IrCall,
        original: IrCall,
        descriptor: ComposableFunctionDescriptor
    ): IrExpression {
        val composerTemp = irTemporary(composerCall)
        val meta = descriptor.composerMetadata
        return irComposableCallBase(
            original,
            { irGet(composerTemp) },
            meta
        )
    }

    private fun IrBlockBuilder.irComposableCallBase(
        original: IrCall,
        getComposer: () -> IrExpression,
        meta: ComposerMetadata
    ): IrExpression {

        /*

        Foo(text="foo")

        // transforms into

        val attr_text = "foo"
        composer.call(
            key = 123,
            invalid = { changed(attr_text) },
            block = { Foo(attr_text) }
        )
         */
        // TODO(lmr): the way we grab temporaries here feels wrong. We should investigate the right
        // way to do this. Additionally, we are creating temporary vars for variables which is
        // causing larger stack space than needed in our generated code.

        val irGetArguments = original
            .symbol
            .descriptor
            .valueParameters
            .map {
                val arg = original.getValueArgument(it)
                it to getParameterExpression(it, arg)
            }

        val tmpDispatchReceiver = original.dispatchReceiver?.let { irTemporary(it) }
        val tmpExtensionReceiver = original.extensionReceiver?.let { irTemporary(it) }

        // TODO(lmr): come up with a better way to find this?
        val callDescriptor = meta
            .callDescriptors
            .first { it.typeParametersCount == 0 }

        val joinKeyDescriptor = meta
            .type
            .memberScope
            .findFirstFunction(KtxNameConventions.JOINKEY.identifier) {
                it.valueParameters.size == 2
            }

        val callParameters = callDescriptor.valueParameters
            .map { it.name to it }
            .toMap()

        fun getCallParameter(name: Name) = callParameters[name]
            ?: error("Expected $name parameter to exist")

        return irCall(
            callee = symbolTable.referenceFunction(callDescriptor),
            type = builtIns.unitType // TODO(lmr): refactor call(...) to return a type
        ).apply {
            dispatchReceiver = getComposer()

            putValueArgument(
                getCallParameter(KtxNameConventions.CALL_KEY_PARAMETER),
                irGroupKey(
                    original = original,
                    getComposer = getComposer,
                    joinKey = joinKeyDescriptor,
                    pivotals = irGetArguments.mapNotNull { (param, getExpr) ->
                        if (!param.hasPivotalAnnotation()) null
                        else getExpr()
                    }
                )
            )

            val invalidParameter = getCallParameter(KtxNameConventions.CALL_INVALID_PARAMETER)

            val validatorType = invalidParameter.type.getReceiverTypeFromFunctionType()
                ?: error("Expected validator type to be on receiver of the invalid lambda")

            val changedDescriptor = validatorType
                .memberScope
                .findFirstFunction("changed") { it.typeParametersCount == 1 }

            val validatedArguments: List<IrExpression> =
                irGetArguments
                    .mapNotNull { (_, getExpr) -> getExpr() } +
                        listOfNotNull(
                            tmpDispatchReceiver?.let { irGet(it) },
                            tmpExtensionReceiver?.let { irGet(it) }
                        )

            val isSkippable = validatedArguments.all { it.type.toKotlinType().isStable() }

            putValueArgument(
                invalidParameter,
                irLambdaExpression(
                    original.startOffset,
                    original.endOffset,
                    descriptor = createFunctionDescriptor(
                        type = invalidParameter.type,
                        owner = symbol.descriptor.containingDeclaration
                    ),
                    type = invalidParameter.type.toIrType()
                ) { fn ->
                    if (!isSkippable) {
                        // if it's not skippable, we don't validate any arguments.
                        +irReturn(irTrue())
                    } else {
                        val validationCalls = validatedArguments
                            .map {
                                irChangedCall(
                                    changedDescriptor = changedDescriptor,
                                    receiver = irGet(fn.extensionReceiverParameter!!),
                                    attributeValue = it
                                )
                            }

                        // all as one expression: a or b or c ... or z
                        +irReturn(when (validationCalls.size) {
                            0 -> irFalse()
                            1 -> validationCalls.single()
                            else -> validationCalls.reduce { accumulator, value ->
                                when {
                                    // if it is a constant, the value is `false`
                                    accumulator is IrConst<*> -> value
                                    value is IrConst<*> -> accumulator
                                    else -> irOr(accumulator, value)
                                }
                            }
                        })
                    }
                }
            )

            val blockParameter = getCallParameter(KtxNameConventions.CALL_BLOCK_PARAMETER)

            putValueArgument(
                blockParameter,
                irLambdaExpression(
                    original.startOffset,
                    original.endOffset,
                    descriptor = createFunctionDescriptor(
                        type = blockParameter.type,
                        owner = symbol.descriptor.containingDeclaration
                    ),
                    type = blockParameter.type.toIrType()
                ) {
                    +irCall(
                        callee = symbolTable.referenceSimpleFunction(original.symbol.descriptor),
                        type = original.type
                    ).apply {
                        copyTypeArgumentsFrom(original)

                        dispatchReceiver = tmpDispatchReceiver?.let { irGet(it) }
                        extensionReceiver = tmpExtensionReceiver?.let { irGet(it) }

                        irGetArguments.forEach { (param, getExpr) ->
                            putValueArgument(param, getExpr())
                        }
                    }
                }
            )
        }
    }

    private fun DeclarationIrBuilder.irComposableExpr(
        composerCall: IrCall,
        original: IrCall,
        descriptor: ComposableCallableDescriptor
    ): IrExpression {
        return irBlock(resultType = descriptor.returnType?.toIrType()) {
            val composerTemp = irTemporary(composerCall)
            val meta = descriptor.composerMetadata
            irComposableExprBase(
                original,
                { irGet(composerTemp) },
                meta
            )
        }
    }

    private fun IrBlockBuilder.irComposableExprBase(
        original: IrCall,
        getComposer: () -> IrExpression,
        meta: ComposerMetadata
    ) {
        /*

        Foo(text="foo")

        // transforms into

        composer.startExpr(123)
        val result = Foo(text="foo")
        composer.endExpr()
        result
         */

        // TODO(lmr): the way we grab temporaries here feels wrong. We should investigate the right
        // way to do this. Additionally, we are creating temporary vars for variables which is
        // causing larger stack space than needed in our generated code.

        val irGetArguments = original
            .symbol
            .descriptor
            .valueParameters
            .map {
                val arg = original.getValueArgument(it)
                it to getParameterExpression(it, arg)
            }

        val startExpr = meta
            .type
            .memberScope
            .findFirstFunction(KtxNameConventions.START_EXPR.identifier) {
                it.valueParameters.size == 1
            }

        val endExpr = meta
            .type
            .memberScope
            .findFirstFunction(KtxNameConventions.END_EXPR.identifier) {
                it.valueParameters.size == 0
            }

        val joinKeyDescriptor = meta
            .type
            .memberScope
            .findFirstFunction(KtxNameConventions.JOINKEY.identifier) {
                it.valueParameters.size == 2
            }

        val startCall = irCall(
            callee = symbolTable.referenceFunction(startExpr),
            type = builtIns.unitType
        ).apply {
            dispatchReceiver = getComposer()
            putValueArgument(
                startExpr.valueParameters.first(),
                irGroupKey(
                    original = original,
                    getComposer = getComposer,
                    joinKey = joinKeyDescriptor,
                    pivotals = irGetArguments.mapNotNull { (param, getExpr) ->
                        if (!param.hasPivotalAnnotation()) null
                        else getExpr()
                    }
                )
            )
        }

        val newCall = irCall(
            callee = symbolTable.referenceSimpleFunction(original.symbol.descriptor),
            type = original.type
        ).apply {
            copyTypeArgumentsFrom(original)

            dispatchReceiver = original.dispatchReceiver
            extensionReceiver = original.extensionReceiver

            irGetArguments.forEach { (param, getExpr) ->
                putValueArgument(param, getExpr())
            }
        }

        val endCall = irCall(
            callee = symbolTable.referenceFunction(endExpr),
            type = builtIns.unitType
        ).apply {
            dispatchReceiver = getComposer()
        }

        val hasResult = !original.type.isUnit()

        if (hasResult) {
            +startCall
            val tmpResult = irTemporary(newCall, irType = original.type)
            +endCall
            +irGet(tmpResult)
        } else {
            +startCall
            +newCall
            +endCall
        }
    }

    private fun IrBlockBuilder.getParameterExpression(
        desc: ValueParameterDescriptor,
        expr: IrExpression?
    ): () -> IrExpression? {
        if (expr == null)
            return { null }
        if (expr.isReorderTemporaryVariable()) {
            return getParameterExpression(desc, expr.unwrapReorderTemporaryVariable())
        }
        return when (expr) {
            is IrConst<*> -> ({ expr.copy() })
            else -> {
                val temp = irTemporary(
                    expr,
                    irType = expr.type
                )
                ({ irGet(temp) })
            }
        }
    }

    private fun IrBuilderWithScope.irGroupKey(
        original: IrCall,
        joinKey: FunctionDescriptor,
        getComposer: () -> IrExpression,
        pivotals: List<IrExpression>
    ): IrExpression {
        val keyValueExpression = irInt(original.sourceLocationHash())
        return if (pivotals.isEmpty()) keyValueExpression
        else (listOf(keyValueExpression) + pivotals).reduce { accumulator, value ->
            irCall(
                callee = symbolTable.referenceFunction(joinKey),
                type = joinKey.returnType!!.toIrType()
            ).apply {
                dispatchReceiver = getComposer()
                putValueArgument(0, accumulator)
                putValueArgument(1, value)
            }
        }
    }

    private fun IrCall.sourceLocationHash(): Int {
        return symbol.descriptor.fqNameSafe.toString().hashCode() xor startOffset
    }

    private fun IrBuilderWithScope.irChangedCall(
        changedDescriptor: FunctionDescriptor,
        receiver: IrExpression,
        attributeValue: IrExpression
    ): IrExpression {
        // TODO(lmr): make it so we can use the "changed" overloads with primitive types.
        // Right now this is causing a lot of boxing/unboxing for primitives
        return if (attributeValue is IrConst<*>) irFalse()
        else irCall(
            callee = symbolTable.referenceFunction(changedDescriptor),
            type = changedDescriptor.returnType?.toIrType()!!
        ).apply {
            putTypeArgument(0, attributeValue.type)
            dispatchReceiver = receiver
            putValueArgument(0, attributeValue)
        }
    }

    private fun IrBuilderWithScope.irOr(
        left: IrExpression,
        right: IrExpression
    ): IrExpression {
        return irCall(
            callee = symbolTable.referenceFunction(orFunctionDescriptor),
            type = builtIns.booleanType
        ).apply {
            dispatchReceiver = left
            putValueArgument(0, right)
        }
    }

    private fun IrBuilderWithScope.irLambdaExpression(
        startOffset: Int,
        endOffset: Int,
        descriptor: FunctionDescriptor,
        type: IrType,
        body: IrBlockBodyBuilder.(IrFunction) -> Unit
    ): IrExpression {
        val symbol = IrSimpleFunctionSymbolImpl(descriptor)

        val returnType = descriptor.returnType!!.toIrType()

        val lambda = IrFunctionImpl(
            startOffset, endOffset,
            IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA,
            symbol,
            returnType
        ).also {
            it.parent = scope.getLocalDeclarationParent()
            it.createParameterDeclarations()
            it.body = DeclarationIrBuilder(context, symbol)
                .irBlockBody { body(it) }
        }

        return irBlock(
            startOffset = startOffset,
            endOffset = endOffset,
            origin = IrStatementOrigin.LAMBDA,
            resultType = type
        ) {
            +lambda
            +IrFunctionReferenceImpl(
                startOffset = startOffset,
                endOffset = endOffset,
                type = type,
                symbol = symbol,
                typeArgumentsCount = descriptor.typeParametersCount,
                origin = IrStatementOrigin.LAMBDA
            )
        }
    }

    private fun IrFunction.createParameterDeclarations() {
        fun ParameterDescriptor.irValueParameter() = IrValueParameterImpl(
            this.startOffset ?: UNDEFINED_OFFSET,
            this.endOffset ?: UNDEFINED_OFFSET,
            IrDeclarationOrigin.DEFINED,
            this,
            type.toIrType(),
            (this as? ValueParameterDescriptor)?.varargElementType?.toIrType()
        ).also {
            it.parent = this@createParameterDeclarations
        }

        fun TypeParameterDescriptor.irTypeParameter() = IrTypeParameterImpl(
            this.startOffset ?: UNDEFINED_OFFSET,
            this.endOffset ?: UNDEFINED_OFFSET,
            IrDeclarationOrigin.DEFINED,
            IrTypeParameterSymbolImpl(this)
        ).also {
            it.parent = this@createParameterDeclarations
        }

        dispatchReceiverParameter = descriptor.dispatchReceiverParameter?.irValueParameter()
        extensionReceiverParameter = descriptor.extensionReceiverParameter?.irValueParameter()

        assert(valueParameters.isEmpty())
        descriptor.valueParameters.mapTo(valueParameters) { it.irValueParameter() }

        assert(typeParameters.isEmpty())
        descriptor.typeParameters.mapTo(typeParameters) { it.irTypeParameter() }
    }

    private fun KotlinType.toIrType(): IrType = typeTranslator.translateType(this)

    private fun IrBuilderWithScope.createFunctionDescriptor(
        type: KotlinType,
        owner: DeclarationDescriptor = scope.scopeOwner
    ): FunctionDescriptor {
        return AnonymousFunctionDescriptor(
            owner,
            Annotations.EMPTY,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            SourceElement.NO_SOURCE,
            false
        ).apply {
            initialize(
                type.getReceiverTypeFromFunctionType()?.let {
                    DescriptorFactory.createExtensionReceiverParameterForCallable(
                        this,
                        it,
                        Annotations.EMPTY
                    )
                },
                null,
                emptyList(),
                type.getValueParameterTypesFromFunctionType().mapIndexed { i, t ->
                    ValueParameterDescriptorImpl(
                        containingDeclaration = this,
                        original = null,
                        index = i,
                        annotations = Annotations.EMPTY,
                        name = t.type.extractParameterNameFromFunctionTypeArgument()
                            ?: Name.identifier("p$i"),
                        outType = t.type,
                        declaresDefaultValue = false,
                        isCrossinline = false,
                        isNoinline = false,
                        varargElementType = null,
                        source = SourceElement.NO_SOURCE
                    )
                },
                type.getReturnTypeFromFunctionType(),
                Modality.FINAL,
                Visibilities.LOCAL,
                null
            )
            isOperator = false
            isInfix = false
            isExternal = false
            isInline = false
            isTailrec = false
            isSuspend = false
            isExpect = false
            isActual = false
        }
    }
}
