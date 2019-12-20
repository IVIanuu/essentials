/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.kotlin.compiler.compose

import com.ivianuu.essentials.kotlin.compiler.asType
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Converter
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Node
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Visitor
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Writer
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.bindingContextUtil.getAbbreviatedTypeOrType
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.model.ArgumentMatch
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.types.typeUtil.isUnit

enum class Step {
    ConvertExpressionComposableFunsToBlocks,
    MergeVarArgToSingleArg,
    moveComposableTrailingLambdasIntoTheBody,
    InsertRestartScopeIntoFunctions,
    WrapComposableLambdasInObserve,
    WrapComposableCalls
}

fun Node.getSeenBy(): MutableSet<Step> {
    return extras.getOrPut("seenBy") { mutableSetOf<Step>() } as MutableSet<Step>
}

fun Node.markSeen(step: Step) {
    getSeenBy().add(step)
}

fun Node.seenBy(step: Step): Boolean = step in getSeenBy()

val ComposerExpr = Node.Expr.BinaryOp(
    lhs = Node.Expr.BinaryOp(
        lhs = Node.Expr.Name(name = "androidx"),
        oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
        rhs = Node.Expr.Name(name = "compose")
    ),
    oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
    rhs = Node.Expr.Name(name = "composer")
)

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    val orig = Converter().convertFile(file)
    val fileNode = Converter().convertFile(file)

    // if (file.name == "CoreHelpers.kt") logFile(fileNode)

    val steps = mutableListOf(
        Step.ConvertExpressionComposableFunsToBlocks,
        Step.MergeVarArgToSingleArg,
        Step.moveComposableTrailingLambdasIntoTheBody,
        Step.WrapComposableLambdasInObserve,
        Step.InsertRestartScopeIntoFunctions,
        Step.WrapComposableCalls
    )
    var unprocessed = collectUnprocessed(fileNode, steps)
    while (unprocessed.isNotEmpty()) {
        mergeVarArgToSingleArg(fileNode, resolveSession, trace)
        moveComposableTrailingLambdasIntoTheBody(fileNode, resolveSession, trace)
        convertExpressionComposableFunsToBlocks(fileNode, resolveSession, trace)
        insertRestartScope(fileNode, resolveSession, trace)
        wrapComposableLambdasInObserve(fileNode, trace)
        wrapComposableCalls(fileNode, resolveSession, trace)
        unprocessed = collectUnprocessed(fileNode, steps)
    }

    val newSource = Writer.write(fileNode)

    // if (file.name == "MainActivity.kt") error("new source $newSource")

    return if (orig != fileNode) {
        file.withNewSource(newSource)
    } else file
}

private fun collectUnprocessed(node: Node, steps: List<Step>): List<Node> {
    val unprocessed = mutableListOf<Node>()
    Visitor.visit(node) { c, _ ->
        if (c == null) return@visit
        val notSeenBy = steps
            .filter { !c.seenBy(it) }
        if (notSeenBy.isNotEmpty()) unprocessed += c
    }
    return unprocessed
}

private fun logFile(fileNode: Node.File) {
    val allNodes = mutableListOf<Node?>()
    Visitor.visit(fileNode) { node, _ ->
        allNodes += node
    }

    error("all nodes $allNodes")
}

private fun mergeVarArgToSingleArg(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node == null) return@visit
        if (node.seenBy(Step.MergeVarArgToSingleArg)) return@visit
        node.markSeen(Step.MergeVarArgToSingleArg)

        if (node !is Node.Expr.Call) return@visit
        val element = node.element as? KtCallExpression ?: return@visit

        val resolvedCall = element.getResolvedCall(trace.bindingContext) ?: return@visit
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) &&
            resulting.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit

        val tmpArgs = node.args.toMutableList()

        val valueParams = node.args
            .map { arg ->
                arg to (resolvedCall.getArgumentMapping(arg.element as KtValueArgument) as? ArgumentMatch)?.valueParameter!!
            }
            .associateBy { it.first }
            .mapValues { it.value.second }

        val valueParamsByName = valueParams.values
            .associateBy { it.name.asString() }

        tmpArgs.forEachIndexed { index, arg ->
            arg.name = arg.name ?: valueParams.getValue(arg).name.asString()
        }

        val newArgs = mutableListOf<Node.ValueArg>()

        val argsByName = tmpArgs
            .groupBy { it.name }
        argsByName.forEach { (name, args) ->
            if (args.size > 1) {
                args.forEach {
                    it.name = null
                }
                newArgs += Node.ValueArg(
                    name = name,
                    asterisk = true,
                    expr = Node.Expr.Call(
                        expr = Node.Expr.Name(name = "arrayOf"),
                        typeArgs = emptyList(),
                        args = args,
                        lambda = null
                    )
                ).apply {
                    this.element = args.first().element
                }
            } else {
                val arg = args.single()
                val argName = arg.name!!
                val valueParam = valueParamsByName.getValue(argName)

                if (valueParam.isVararg) {
                    arg.name = null
                    newArgs += Node.ValueArg(
                        name = name,
                        asterisk = valueParamsByName.getValue(argName).isVararg,
                        expr = Node.Expr.Call(
                            expr = Node.Expr.Name(name = "arrayOf"),
                            typeArgs = emptyList(),
                            args = args,
                            lambda = null
                        )
                    ).apply {
                        this.element = arg.element
                    }
                } else {
                    newArgs += arg
                }
            }
        }

        node.args = newArgs
    }
}

private fun convertExpressionComposableFunsToBlocks(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node == null) return@visit
        if (node.seenBy(Step.ConvertExpressionComposableFunsToBlocks)) return@visit
        node.markSeen(Step.ConvertExpressionComposableFunsToBlocks)

        if (node !is Node.Decl.Func) return@visit
        if (node.body == null) return@visit
        val element = node.element as? KtNamedFunction ?: return@visit
        val descriptor = try {
            resolveSession.resolveToDescriptor(element) as FunctionDescriptor
        } catch (e: Exception) {
            null
        } ?: return@visit
        if (!descriptor.annotations.hasAnnotation(ComposableAnnotation) &&
            descriptor.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit
        if (descriptor.returnType?.isUnit() != true) return@visit
        if (node.body !is Node.Decl.Func.Body.Expr) return@visit

        val body = node.body as Node.Decl.Func.Body.Expr
        val block = Node.Block(
            stmts = listOf(
                Node.Stmt.Expr(expr = body.expr)
            )
        )
        node.body = Node.Decl.Func.Body.Block(block = block)
    }
}

private fun moveComposableTrailingLambdasIntoTheBody(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node == null) return@visit
        if (node.seenBy(Step.moveComposableTrailingLambdasIntoTheBody)) return@visit
        node.markSeen(Step.moveComposableTrailingLambdasIntoTheBody)

        if (node !is Node.Expr.Call) return@visit
        val lambda = node.lambda ?: return@visit

        val element = node.element as? KtCallExpression ?: return@visit
        val resolvedCall = element.getResolvedCall(trace.bindingContext) ?: return@visit
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) &&
            resulting.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit

        val funcName = resulting.name.asString()

        val newArgs = node.args.toMutableList()

        newArgs.forEachIndexed { index, arg ->
            arg.name = arg.name
                ?: (resolvedCall.getArgumentMapping(arg.element as KtValueArgument) as? ArgumentMatch)?.valueParameter?.name?.asString()
        }

        val lambdaDescriptor = resulting.valueParameters.last()

        val lambdaArgName = lambdaDescriptor.name.asString()
        val newLambdaExpr = Node.Expr.Labeled(
            label = lambdaArgName,
            expr = lambda.func
        )

        Visitor.visit(lambda.func) { childNode, _ ->
            when (childNode) {
                is Node.Expr.This -> if (childNode.label == funcName) childNode.label =
                    lambdaArgName
                is Node.Expr.Super -> if (childNode.label == funcName) childNode.label =
                    lambdaArgName
                is Node.Expr.Return -> if (childNode.label == funcName) childNode.label =
                    lambdaArgName
                is Node.Expr.Continue -> if (childNode.label == funcName) childNode.label =
                    lambdaArgName
                is Node.Expr.Break -> if (childNode.label == funcName) childNode.label =
                    lambdaArgName
            }
        }

        newArgs += Node.ValueArg(
            name = lambdaArgName,
            asterisk = false,
            expr = newLambdaExpr
        ).apply {
            this.element = lambda.element
        }

        node.args = newArgs
        node.lambda = null
    }
}

private fun execExpr(stmts: List<Node.Stmt>): Node.Expr {
    return Node.Expr.BinaryOp(
        lhs = Node.Expr.BinaryOp(
            lhs = Node.Expr.BinaryOp(
                lhs = Node.Expr.BinaryOp(
                    lhs = Node.Expr.BinaryOp(
                        lhs = Node.Expr.Name(name = "com"),
                        oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                        rhs = Node.Expr.Name(name = "ivianuu")
                    ),
                    oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                    rhs = Node.Expr.Name(name = "essentials")
                ),
                oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                rhs = Node.Expr.Name(name = "ui")
            ),
            oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
            rhs = Node.Expr.Name(name = "core")
        ),
        oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
        rhs = Node.Expr.Call(
            expr = Node.Expr.Name(name = "exec"),
            typeArgs = emptyList(),
            args = emptyList(),
            lambda = Node.Expr.Call.TrailLambda(
                anns = emptyList(),
                label = null,
                func = Node.Expr.Brace(
                    params = emptyList(),
                    block = Node.Block(
                        stmts = stmts
                    )
                )
            )
        )
    )
}

private inline fun <reified T> Node.findAnchestorOfType(): T? {
    var parent: Node = parent ?: return null
    while (parent.parent != null) {
        parent = parent.parent!!
        if (parent is T) return parent
    }
    return null
}

private fun Node.Block.invokesComposables(
    trace: BindingTrace
): Boolean {
    var invokesComposables = false
    Visitor.visit(this) { childNode, _ ->
        if (invokesComposables) return@visit
        if (childNode !is Node.Expr.Call) return@visit
        if (childNode.findAnchestorOfType<Node.Block>() != this) return@visit
        val element = childNode.element as? KtCallExpression ?: return@visit
        val resolvedCall = element.getResolvedCall(trace.bindingContext) ?: return@visit
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) &&
            resulting.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit
        invokesComposables = true
    }

    return invokesComposables
}

private fun wrapComposableLambdasInObserve(
    file: Node.File,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, _ ->
        if (node == null) return@visit
        if (node.seenBy(Step.WrapComposableLambdasInObserve)) return@visit
        node.markSeen(Step.WrapComposableLambdasInObserve)

        if (node !is Node.Expr.Brace) return@visit

        val parent = node.parent

        if (parent is Node.Expr.If ||
            parent is Node.Expr.For ||
            parent is Node.Expr.While ||
            parent is Node.Expr.When ||
            parent is Node.Expr.When.Entry
        ) return@visit

        var parentWithType: Node? = parent
        while (parentWithType != null) {
            if (parentWithType is Node.Decl.Func.Param ||
                parentWithType is Node.Decl.Property ||
                parentWithType is Node.ValueArg ||
                parentWithType is Node.Expr.Call.TrailLambda
            ) {
                break
            }

            parentWithType = parentWithType.parent
        }

        (node.element as? KtFunctionLiteral)?.let {
            if (InlineUtil.isInlinedArgument(it, trace.bindingContext, false)) return@visit
        }

        var isUnitComposable = false

        when (parentWithType) {
            is Node.Decl.Func.Param -> {
                val paramElement = parentWithType.element as KtParameter
                val type =
                    paramElement.typeReference?.getAbbreviatedTypeOrType(trace.bindingContext)
                        ?: return@visit
                if (type.annotations.hasAnnotation(ComposableAnnotation) &&
                    type.isFunctionType &&
                    type.getReturnTypeFromFunctionType().isUnit()
                ) {
                    isUnitComposable = true
                }
            }
            is Node.Decl.Property -> {
                val propertyElement = parentWithType.element as KtProperty
                val type =
                    propertyElement.typeReference?.getAbbreviatedTypeOrType(trace.bindingContext)
                        ?: return@visit
                if (type.annotations.hasAnnotation(ComposableAnnotation) &&
                    type.isFunctionType &&
                    type.getReturnTypeFromFunctionType().isUnit()
                ) {
                    isUnitComposable = true
                }
            }
            is Node.ValueArg, is Node.Expr.Call.TrailLambda -> {
                when (val valueArgParent = parentWithType.parent) {
                    is Node.Decl.EnumEntry, is Node.Expr.Call -> {
                        val element = valueArgParent.element as? KtCallExpression ?: return@visit
                        val resolvedCall =
                            element.getResolvedCall(trace.bindingContext) ?: return@visit
                        val resulting = resolvedCall.resultingDescriptor as FunctionDescriptor

                        val argDescriptor = if (parentWithType is Node.ValueArg) {
                            (resolvedCall.getArgumentMapping(parentWithType.element as KtValueArgument) as? ArgumentMatch)?.valueParameter!!
                        } else resulting.valueParameters.last()

                        val notInlined = !resulting.isInline || argDescriptor.isNoinline
                        if (notInlined &&
                            argDescriptor.type.annotations.hasAnnotation(ComposableAnnotation) &&
                            argDescriptor.type.isFunctionType &&
                            argDescriptor.type.getReturnTypeFromFunctionType().isUnit()
                        ) {
                            isUnitComposable = true
                        }
                    }
                }
            }
        }

        if (isUnitComposable) {
            val oldBlock = node.block
            node.block = Node.Block(
                stmts = listOf(
                    Node.Stmt.Expr(
                        Node.Expr.Call(
                            expr = Node.Expr.BinaryOp(
                                lhs = Node.Expr.BinaryOp(
                                    lhs = Node.Expr.Name(name = "androidx"),
                                    oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                                    rhs = Node.Expr.Name(name = "compose")
                                ),
                                oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                                rhs = Node.Expr.Name(name = "Observe")
                            ),
                            typeArgs = emptyList(),
                            args = listOf(
                                Node.ValueArg(
                                    name = "body",
                                    asterisk = false,
                                    expr = Node.Expr.Brace(
                                        params = emptyList(),
                                        block = oldBlock
                                    )
                                )
                            ),
                            lambda = null
                        )
                    )
                )
            )
        }
    }
}

private fun insertRestartScope(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node == null) return@visit
        if (node.seenBy(Step.InsertRestartScopeIntoFunctions)) return@visit
        node.markSeen(Step.InsertRestartScopeIntoFunctions)

        if (node !is Node.Decl.Func) return@visit
        if (node.body !is Node.Decl.Func.Body.Block) return@visit
        if (!(node.body as Node.Decl.Func.Body.Block).block.invokesComposables(trace)) return@visit

        val element = node.element as? KtNamedFunction ?: return@visit
        val descriptor = try {
            resolveSession.resolveToDescriptor(element) as FunctionDescriptor
        } catch (e: Exception) {
            null
        } ?: return@visit
        if (descriptor.isInline) return@visit
        if (!descriptor.annotations.hasAnnotation(ComposableAnnotation) &&
            descriptor.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit
        if (descriptor.returnType?.isUnit() != true) return@visit

        val block = (node.body as Node.Decl.Func.Body.Block).block
        val oldStmts = block.stmts

        val funcKey = "${descriptor.fqNameSafe.asString()}:${element.startOffset}".hashCode()

        val startRestartGroupStmt = Node.Stmt.Expr(
            expr = Node.Expr.BinaryOp(
                lhs = ComposerExpr,
                oper = Node.Expr.BinaryOp.Oper.Token(Node.Expr.BinaryOp.Token.DOT),
                rhs = Node.Expr.Call(
                    expr = Node.Expr.Name(name = "startRestartGroup"),
                    typeArgs = emptyList(),
                    args = listOf(
                        Node.ValueArg(
                            name = null,
                            asterisk = false,
                            expr = Node.Expr.Const(
                                value = "$funcKey",
                                form = Node.Expr.Const.Form.INT
                            )
                        )
                    ),
                    lambda = null
                )
            )
        )

        val newStmts = mutableListOf<Node.Stmt>()
        newStmts += startRestartGroupStmt

        val endRestartGroupStmt = Node.Stmt.Expr(
            expr = Node.Expr.BinaryOp(
                lhs = Node.Expr.BinaryOp(
                    lhs = ComposerExpr,
                    oper = Node.Expr.BinaryOp.Oper.Token(Node.Expr.BinaryOp.Token.DOT),
                    rhs = Node.Expr.Call(
                        expr = Node.Expr.Name(name = "endRestartGroup"),
                        typeArgs = emptyList(),
                        args = emptyList(),
                        lambda = null
                    )
                ),
                oper = Node.Expr.BinaryOp.Oper.Token(Node.Expr.BinaryOp.Token.DOT_SAFE),
                rhs = Node.Expr.Call(
                    expr = Node.Expr.Name(name = "updateScope"),
                    typeArgs = emptyList(),
                    args = emptyList(),
                    lambda = Node.Expr.Call.TrailLambda(
                        anns = emptyList(),
                        label = null,
                        func = Node.Expr.Brace(
                            params = emptyList(),
                            block = Node.Block(
                                stmts = listOf(
                                    Node.Stmt.Expr(
                                        expr = Node.Expr.Call(
                                            expr = Node.Expr.Name(name = descriptor.fqNameSafe.shortName().asString()),
                                            typeArgs = descriptor.typeParameters.map { typeParam ->
                                                Node.Type(
                                                    mods = emptyList(),
                                                    ref = Node.TypeRef.Simple(
                                                        pieces = listOf(
                                                            Node.TypeRef.Simple.Piece(
                                                                name = typeParam.name.asString(),
                                                                typeParams = emptyList()
                                                            )
                                                        )
                                                    )
                                                )
                                            },
                                            args = descriptor.valueParameters.map { valueParam ->
                                                Node.ValueArg(
                                                    name = valueParam.name.asString(),
                                                    asterisk = valueParam.isVararg,
                                                    expr = Node.Expr.Name(name = valueParam.name.asString())
                                                )
                                            },
                                            lambda = null
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        newStmts += Node.Stmt.Expr(
            expr = Node.Expr.Try(
                block = Node.Block(stmts = oldStmts),
                catches = emptyList(),
                finallyBlock = Node.Block(
                    stmts = listOf(endRestartGroupStmt)
                )
            )
        )

        block.stmts = newStmts
    }
}

private fun wrapComposableCalls(
    fileNode: Node,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    var keyIndex = 0
    fun nextKeyIndex() = ++keyIndex
    val file = (fileNode.element as KtFile)
    Visitor.visit(fileNode) { node, parent ->
        if (node == null) return@visit

        if (node.seenBy(Step.WrapComposableCalls)) return@visit
        node.markSeen(Step.WrapComposableCalls)

        val resolvedCall = when {
            node is Node.Expr.Call -> {
                val element = node.element as? KtCallExpression ?: return@visit
                element.getResolvedCall(trace.bindingContext)
            }
            node is Node.Expr.Name && node.parent !is Node.Expr.Call -> {
                val element = node.element as? KtSimpleNameExpression ?: return@visit
                element.getResolvedCall(trace.bindingContext)
            }
            else -> null
        } ?: return@visit

        node as Node.Expr

        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) &&
            resulting.overriddenDescriptors.none {
                it.annotations.hasAnnotation(
                ComposableAnnotation) }) return@visit
        val isEffect = resulting.returnType?.isUnit() == false

        var rootExpr: Node.Expr = node
        while (rootExpr.parent is Node.Expr.BinaryOp &&
            (rootExpr.parent as Node.Expr.BinaryOp).oper is Node.Expr.BinaryOp.Oper.Token &&
            ((rootExpr.parent as Node.Expr.BinaryOp).oper as Node.Expr.BinaryOp.Oper.Token).token == Node.Expr.BinaryOp.Token.DOT
        ) {
            rootExpr = rootExpr.parent as Node.Expr
        }

        val callKey =
            "${file.packageFqName.asString()}:${file.name}:${node.element!!.startOffset}".hashCode()
        val callKeyIndex = nextKeyIndex()

        val initKeyStmt = Node.Stmt.Decl(
            decl = Node.Decl.Property(
                mods = emptyList(),
                readOnly = false,
                typeParams = emptyList(),
                receiverType = null,
                vars = listOf(
                    Node.Decl.Property.Var(
                        name = "key_$callKeyIndex",
                        type = Node.Type(
                            mods = emptyList(),
                            ref = Node.TypeRef.Simple(
                                pieces = listOf(
                                    Node.TypeRef.Simple.Piece(
                                        name = "Any",
                                        typeParams = emptyList()
                                    )
                                )
                            )
                        )
                    )
                ),
                typeConstraints = emptyList(),
                delegated = false,
                expr = Node.Expr.Const(value = "$callKey", form = Node.Expr.Const.Form.INT),
                accessors = null
            )
        )

        fun initArgStmt(
            arg: Node.ValueArg,
            resolvedCall: ResolvedCall<*>,
            index: Int
        ): Node.Stmt.Decl {
            val descriptor =
                (resolvedCall.getArgumentMapping(arg.element as KtValueArgument) as? ArgumentMatch)?.valueParameter!!

            val argExpr = arg.expr
            val oldLabel =
                if (argExpr is Node.Expr.Labeled) argExpr.label else resolvedCall.resultingDescriptor.name.asString()
            val argName = "arg_${callKeyIndex}_$index"
            if (argExpr is Node.Expr.Labeled) argExpr.label = argName
            Visitor.visit(argExpr) { childNode, _ ->
                when (childNode) {
                    is Node.Expr.This -> if (childNode.label == oldLabel) childNode.label = argName
                    is Node.Expr.Super -> if (childNode.label == oldLabel) childNode.label = argName
                    is Node.Expr.Return -> if (childNode.label == oldLabel) childNode.label =
                        argName
                    is Node.Expr.Continue -> if (childNode.label == oldLabel) childNode.label =
                        argName
                    is Node.Expr.Break -> if (childNode.label == oldLabel) childNode.label = argName
                }
            }

            val newArgExpr =
                if ((descriptor.type.isFunctionType || descriptor.type.isSuspendFunctionType) && argExpr is Node.Expr.Brace) {
                    Node.Expr.Labeled(
                        label = argName,
                        expr = arg.expr
                    )
                } else arg.expr

            return Node.Stmt.Decl(
                decl = Node.Decl.Property(
                    mods = emptyList(),
                    readOnly = false,
                    typeParams = emptyList(),
                    receiverType = null,
                    vars = listOf(
                        Node.Decl.Property.Var(
                            name = argName,
                            type = descriptor.type.asType()
                        )
                    ),
                    typeConstraints = emptyList(),
                    delegated = false,
                    expr = newArgExpr,
                    accessors = null
                )
            )
        }

        fun joinKeyStmt(
            argName: String
        ) = Node.Stmt.Expr(
            expr = Node.Expr.BinaryOp(
                lhs = Node.Expr.Name(name = "key_$callKeyIndex"),
                oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.ASSN),
                rhs = Node.Expr.BinaryOp(
                    lhs = ComposerExpr,
                    oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                    rhs = Node.Expr.Call(
                        expr = Node.Expr.Name(name = "joinKey"),
                        typeArgs = emptyList(),
                        args = listOf(
                            Node.ValueArg(
                                name = null,
                                asterisk = false,
                                expr = Node.Expr.Name(name = "key_$callKeyIndex")
                            ),
                            Node.ValueArg(
                                name = null,
                                asterisk = false,
                                expr = Node.Expr.Name(name = argName)
                            )
                        ),
                        lambda = null
                    )
                )
            )
        )

        fun composerChangedExpr(
            argName: String,
            next: Node.Expr?
        ): Node.Expr {
            val changedExpr = Node.Expr.Call(
                expr = Node.Expr.Name(name = "changed"),
                typeArgs = emptyList(),
                args = listOf(
                    Node.ValueArg(
                        name = null,
                        asterisk = false,
                        expr = Node.Expr.Name(name = argName)
                    )
                ),
                lambda = null
            )

            return if (next == null) changedExpr else Node.Expr.BinaryOp(
                lhs = changedExpr,
                oper = Node.Expr.BinaryOp.Oper.Infix(str = "or"),
                rhs = next
            )
        }

        fun composerExprStmt() = Node.Stmt.Expr(
            expr = Node.Expr.BinaryOp(
                lhs = ComposerExpr,
                oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                rhs = Node.Expr.Call(
                    expr = Node.Expr.Name(name = "expr"),
                    typeArgs = emptyList(),
                    args = listOf(
                        Node.ValueArg(
                            name = "key",
                            asterisk = false,
                            expr = Node.Expr.Name(name = "key_$callKeyIndex")
                        ),
                        Node.ValueArg(
                            name = "block",
                            asterisk = false,
                            expr = Node.Expr.Brace(
                                params = emptyList(),
                                block = Node.Block(
                                    stmts = listOf(Node.Stmt.Expr(expr = rootExpr))
                                )
                            )
                        )
                    ),
                    lambda = null
                )
            )
        )

        fun composerCallStmt() = Node.Stmt.Expr(
            expr = Node.Expr.BinaryOp(
                lhs = ComposerExpr,
                oper = Node.Expr.BinaryOp.Oper.Token(token = Node.Expr.BinaryOp.Token.DOT),
                rhs = Node.Expr.Call(
                    expr = Node.Expr.Name(name = "call"),
                    typeArgs = emptyList(),
                    args = listOf(
                        Node.ValueArg(
                            name = "key",
                            asterisk = false,
                            expr = Node.Expr.Name(name = "key_$callKeyIndex")
                        ),
                        Node.ValueArg(
                            name = "invalid",
                            asterisk = false,
                            expr = Node.Expr.Brace(
                                params = emptyList(),
                                block = Node.Block(
                                    stmts = mutableListOf<Node.Stmt>().also { stmts ->
                                        if (node !is Node.Expr.Call || node.args.isEmpty()) {
                                            stmts += Node.Stmt.Expr(
                                                expr = Node.Expr.Const(
                                                    value = "false",
                                                    form = Node.Expr.Const.Form.BOOLEAN
                                                )
                                            )
                                        } else {
                                            val stableParams = node.args
                                                .mapIndexed { index, arg ->
                                                    Triple(
                                                        arg,
                                                        index,
                                                        resulting.valueParameters.first { param ->
                                                            if (arg.name != null) {
                                                                param.name.asString() == arg.name
                                                            } else {
                                                                resulting.valueParameters.indexOf(
                                                                    param
                                                                ) == index
                                                            }
                                                        }
                                                    )
                                                }
                                                .filter { it.third.type.isStable() }

                                            if (resulting.annotations.hasAnnotation(
                                                    UnstableAnnotation
                                                ) ||
                                                stableParams.size != node.args.size
                                            ) {
                                                stmts += Node.Stmt.Expr(
                                                    expr = Node.Expr.Const(
                                                        value = "true",
                                                        form = Node.Expr.Const.Form.BOOLEAN
                                                    )
                                                )
                                            } else {
                                                stmts += Node.Stmt.Expr(
                                                    expr = stableParams.foldRight(null) { argTriple: Triple<Node.ValueArg, Int, ValueParameterDescriptor>, next: Node.Expr? ->
                                                        composerChangedExpr(
                                                            argName = "arg_${callKeyIndex}_${argTriple.second}",
                                                            next = next
                                                        )
                                                    }!!
                                                )
                                            }
                                        }
                                    }
                                )
                            )
                        ),
                        Node.ValueArg(
                            name = "block",
                            asterisk = false,
                            expr = Node.Expr.Brace(
                                params = emptyList(),
                                block = Node.Block(
                                    stmts = listOf(
                                        Node.Stmt.Expr(
                                            expr = rootExpr
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    lambda = null
                )
            )
        )

        val newExpr = execExpr(
            mutableListOf<Node.Stmt>().also { stmts ->
                stmts += initKeyStmt
                if (node is Node.Expr.Call) {
                    node.args.forEachIndexed { index, arg ->
                        stmts += initArgStmt(arg, resolvedCall, index)
                    }

                    resulting.valueParameters.forEachIndexed { index, param ->
                        if (param.annotations.hasAnnotation(PivotalAnnotation)) {
                            stmts += joinKeyStmt("arg_${callKeyIndex}_$index")
                        }
                    }
                }

                stmts += if (isEffect) {
                    composerExprStmt()
                } else {
                    composerCallStmt()
                }
            }
        )

        if (node is Node.Expr.Call) {
            node.args = node.args
                .mapIndexed { index, arg ->
                    Node.ValueArg(
                        name = arg.name,
                        asterisk = arg.asterisk,
                        expr = Node.Expr.Name(name = "arg_${callKeyIndex}_$index")
                    )
                }
        }

        when (val rootExprParent = rootExpr.parent) {
            is Node.Decl.Func.Body.Expr -> rootExprParent.expr = newExpr
            is Node.Decl.Structured.Parent.Type -> rootExprParent.by = newExpr
            is Node.Decl.Func.Param -> rootExprParent.default = newExpr
            is Node.Decl.Property -> rootExprParent.expr = newExpr
            is Node.ValueArg -> rootExprParent.expr = newExpr
            is Node.Expr.If -> {
                when {
                    rootExprParent.expr === rootExpr -> rootExprParent.expr = newExpr
                    rootExprParent.body === rootExpr -> rootExprParent.body = newExpr
                    rootExprParent.elseBody === rootExpr -> rootExprParent.elseBody = newExpr
                }
            }
            is Node.Expr.For -> {
                when {
                    rootExprParent.inExpr === rootExpr -> rootExprParent.inExpr = newExpr
                    rootExprParent.body === rootExpr -> rootExprParent.body = newExpr
                }
            }
            is Node.Expr.While -> {
                when {
                    rootExprParent.expr === rootExpr -> rootExprParent.expr = newExpr
                    rootExprParent.body === rootExpr -> rootExprParent.body = newExpr
                }
            }
            is Node.Expr.BinaryOp -> {
                when {
                    rootExprParent.lhs === rootExpr -> rootExprParent.lhs = newExpr
                    rootExprParent.rhs === rootExpr -> rootExprParent.rhs = newExpr
                }
            }
            is Node.Expr.UnaryOp -> rootExprParent.expr = newExpr
            is Node.Expr.TypeOp -> rootExprParent.lhs = newExpr
            is Node.Expr.Paren -> rootExprParent.expr = newExpr
            is Node.Expr.StringTmpl.Elem.ShortTmpl -> rootExprParent.expr = newExpr
            is Node.Expr.StringTmpl.Elem.LongTmpl -> rootExprParent.expr = newExpr
            is Node.Expr.When -> rootExprParent.expr = newExpr
            is Node.Expr.When.Entry -> rootExprParent.body = newExpr
            is Node.Expr.Throw -> rootExprParent.expr = newExpr
            is Node.Expr.Return -> rootExprParent.expr = newExpr
            is Node.Expr.CollLit -> {
                val newExprs = rootExprParent.exprs.toMutableList()
                val index = rootExprParent.exprs.indexOf(rootExpr)
                newExprs.removeAt(index)
                newExprs.add(index, newExpr)
                rootExprParent.exprs = newExprs
            }
            is Node.Expr.Labeled -> rootExprParent.expr = newExpr
            is Node.Expr.Annotated -> rootExprParent.expr = newExpr
            is Node.Expr.Call -> rootExprParent.expr = newExpr
            is Node.Expr.ArrayAccess -> {
                if (rootExprParent.expr === rootExpr) {
                    rootExprParent.expr = newExpr
                } else {
                    val newIndices = rootExprParent.indices.toMutableList()
                    val index = rootExprParent.indices.indexOf(rootExpr)
                    newIndices.removeAt(index)
                    newIndices.add(index, newExpr)
                    rootExprParent.indices = newIndices
                }
            }
            is Node.Stmt.Expr -> rootExprParent.expr = newExpr
            else -> {
                error("$rootExpr -> ${rootExpr.parent?.javaClass} ${rootExpr.parent}")
            }
        }
    }
}
