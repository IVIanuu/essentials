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
import com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ArgumentMatch
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    val fileNode = Converter().convertFile(file)

    // logFile(fileNode)

    wrapComposableLambdasInObserve(fileNode, resolveSession, trace)
    moveComposableTrailingLambdasIntoTheBody(fileNode, resolveSession, trace)
    insertRestartScope(fileNode, resolveSession, trace)
    wrapComposableCalls(fileNode, resolveSession, trace)

    val newSource = Writer.write(fileNode)

    //error("new source $newSource")

    return file.withNewSource(newSource)
}

private fun logFile(fileNode: Node.File) {
    val allNodes = mutableListOf<Node?>()
    Visitor.visit(fileNode) { node, _ ->
        allNodes += node
    }

    error("all nodes $allNodes")
}

private fun moveComposableTrailingLambdasIntoTheBody(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node !is Node.Expr.Call) return@visit
        val element = node.element as? KtCallExpression ?: return@visit
        val lambda = node.lambda ?: return@visit
        val resolvedCall = element.getResolvedCall(trace.bindingContext)!!
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation)) return@visit
        if (!resolvedCall.candidateDescriptor.hasStableParameterNames()) return@visit

        val newArgs = node.args.toMutableList()
        newArgs.forEachIndexed { index, arg ->
            arg.name = arg.name
                ?: (resolvedCall.getArgumentMapping(arg.element as KtValueArgument) as? ArgumentMatch)?.valueParameter?.name?.asString()
        }

        val lambdaArgName = resulting.valueParameters.last()
        newArgs.add(
            Node.ValueArg(
                name = lambdaArgName.name.asString(),
                asterisk = false,
                expr = lambda.func
            ).also { it.element = lambda.element }
        )

        node.args = newArgs
        node.lambda = null
    }

}

private fun wrapComposableLambdasInObserve(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    Visitor.visit(file) { node, parent ->
        if (node !is Node.Expr.Brace) return@visit

        var isComposable = false

        if (node.parent is Node.Decl.Property) {
            val property = node.parent as Node.Decl.Property
            val firstVar = property.vars.singleOrNull()
            if (firstVar != null && firstVar.type?.anns?.any { set ->
                    set.anns.any { it.names.first() == "Composable" }
                } == true) {
                isComposable = true
            }
        }

        if (!isComposable && node.parent is Node.ValueArg) {
            val arg = node.parent as Node.ValueArg
            val call = arg.parent as Node.Expr.Call
            val argIndex = call.args.indexOf(arg)
            val element = call.element as? KtCallExpression ?: return@visit
            val resolvedCall = element.getResolvedCall(trace.bindingContext)!!
            val resulting = resolvedCall.resultingDescriptor
            val argDescriptor = resulting.valueParameters[argIndex]

            if (argDescriptor.type.annotations.hasAnnotation(ComposableAnnotation)) {
                isComposable = true
            }
        }

        if (!isComposable && node.parent is Node.Expr.Call.TrailLambda) {
            val lambda = node.parent as Node.Expr.Call.TrailLambda
            val call = lambda.parent as Node.Expr.Call
            val element = call.element as? KtCallExpression ?: return@visit
            val resolvedCall = element.getResolvedCall(trace.bindingContext)!!
            val resulting = resolvedCall.resultingDescriptor
            val lambdaArgDescriptor = resulting.valueParameters.last()

            if (lambdaArgDescriptor.type.annotations.hasAnnotation(ComposableAnnotation)) {
                isComposable = true
            }
        }

        if (isComposable) {
            val oldBlock = node.block
            node.block = Node.Block(
                stmts = listOf(
                    Node.Stmt.Expr(
                        Node.Expr.Call(
                            expr = Node.Expr.Name("androidx.compose.Observe", true),
                            typeArgs = emptyList(),
                            args = emptyList(),
                            lambda = Node.Expr.Call.TrailLambda(
                                emptyList(),
                                null,
                                func = Node.Expr.Brace(
                                    params = emptyList(),
                                    block = oldBlock
                                )
                            )
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
        if (node !is Node.Decl.Func) return@visit
        val body = node.body as? Node.Decl.Func.Body.Block ?: return@visit
        val block = body.block
        val element = node.element as? KtNamedFunction ?: return@visit
        val descriptor =
            resolveSession.resolveToDescriptor(element) as FunctionDescriptor
        if (!descriptor.annotations.hasAnnotation(ComposableAnnotation)) return@visit
        if (descriptor.returnType?.isUnit() != true) return@visit

        var composableCalls = 0
        Visitor.visit(node) { childNode, _ ->
            if (childNode !is Node.Expr.Call) return@visit
            val childElement = childNode.element as? KtCallExpression ?: return@visit
            val resolvedCall = childElement.getResolvedCall(trace.bindingContext)!!
            val resulting = resolvedCall.resultingDescriptor
            if (!resulting.annotations.hasAnnotation(ComposableAnnotation) || resulting.returnType?.isUnit() == false) return@visit

            ++composableCalls
        }

        if (composableCalls == 0) return@visit

        val funcKey = "${descriptor.fqNameSafe.asString()}:${element.startOffset}".hashCode()
        val newStmts = block.stmts.toMutableList()

        val getComposerStmt = Node.Stmt.Expr(
            Node.Expr.Text("val compose_composer = androidx.compose.composer")
        )
        newStmts.add(0, getComposerStmt)

        val startRestartGroupStmt = Node.Stmt.Expr(
            Node.Expr.Text("compose_composer.startRestartGroup($funcKey)")
        )
        newStmts.add(1, startRestartGroupStmt)

        val endRestartGroupStmt = Node.Stmt.Expr(
            Node.Expr.Text(
                CodeBlock.builder()
                    .beginControlFlow("compose_composer.endRestartGroup()?.updateScope {")
                    .addStatement(
                        "${descriptor.name}(${descriptor.valueParameters.map { it.name }.joinToString(
                            ", "
                        )})"
                    )
                    .endControlFlow()
                    .build()
                    .toString()
            )
        )
        newStmts += endRestartGroupStmt

        block.stmts = newStmts
    }
}

private fun wrapComposableCalls(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    var func: Node.Decl.Func? = null
    var keyIndex = 0
    fun nextKeyIndex() = ++keyIndex
    Visitor.visit(file) { node, parent ->
        if (node is Node.Decl.Func) {
            func = node
            keyIndex = 0
        }
        if (node !is Node.Expr.Call) return@visit
        if (func == null) return@visit
        val element = node.element as? KtCallExpression ?: return@visit
        val resolvedCall = element.getResolvedCall(trace.bindingContext)!!
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation)) return@visit
        val isEffect = resulting.returnType?.isUnit() == false

        var rootExpr: Node.Expr = node
        while (rootExpr.parent is Node.Expr.BinaryOp &&
            (rootExpr.parent as Node.Expr.BinaryOp).oper is Node.Expr.BinaryOp.Oper.Token &&
            ((rootExpr.parent as Node.Expr.BinaryOp).oper as Node.Expr.BinaryOp.Oper.Token).token == Node.Expr.BinaryOp.Token.DOT
        ) {
            rootExpr = rootExpr.parent as Node.Expr
        }

        val funcDescriptor = resolveSession.resolveToDescriptor(func!!.element!! as KtNamedFunction)

        val callKey = "${funcDescriptor.fqNameSafe.asString()}:${element.startOffset}".hashCode()
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
            return Node.Stmt.Decl(
                decl = Node.Decl.Property(
                    mods = emptyList(),
                    readOnly = false,
                    typeParams = emptyList(),
                    receiverType = null,
                    vars = listOf(
                        Node.Decl.Property.Var(
                            name = "arg_${callKeyIndex}_${index}",
                            type = descriptor.type.asType()
                        )
                    ),
                    typeConstraints = emptyList(),
                    delegated = false,
                    expr = arg.expr,
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
                    lhs = Node.Expr.Name(name = "compose_composer"),
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
                lhs = Node.Expr.Name("compose_composer"),
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
                                    stmts = listOf(
                                        Node.Stmt.Expr(expr = rootExpr)
                                    )
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
                lhs = Node.Expr.Name("compose_composer"),
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
                                        if (node.args.isEmpty()) {
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

                                            if (stableParams.size != node.args.size) {
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

        val newExpr = Node.Expr.Call(
            expr = Node.Expr.Name(
                name = "with"
            ),
            typeArgs = emptyList(),
            args = listOf(
                Node.ValueArg(
                    name = null,
                    asterisk = false,
                    expr = Node.Expr.Name(
                        name = "Unit"
                    )
                )
            ),
            lambda = Node.Expr.Call.TrailLambda(
                anns = emptyList(),
                label = null,
                func = Node.Expr.Brace(
                    params = emptyList(),
                    block = Node.Block(
                        stmts = mutableListOf<Node.Stmt>().also { stmts ->
                            stmts += initKeyStmt
                            node.args.forEachIndexed { index, arg ->
                                stmts += initArgStmt(arg, resolvedCall, index)
                            }

                            resulting.valueParameters.forEachIndexed { index, param ->
                                if (param.annotations.hasAnnotation(PivotalAnnotation)) {
                                    stmts += joinKeyStmt("arg_${callKeyIndex}_${index}")
                                }
                            }

                            if (isEffect) {
                                stmts += composerExprStmt()
                            } else {
                                stmts += composerCallStmt()
                            }
                        }
                    )
                )
            )
        )


        node.expr = Node.Expr.Call(
            expr = node.expr,
            typeArgs = node.typeArgs,
            args = node.args
                .mapIndexed { index, arg ->
                    Node.ValueArg(
                        name = arg.name,
                        asterisk = false, // todo
                        expr = Node.Expr.Name(name = "arg_${callKeyIndex}_${index}")
                    )
                },
            lambda = null
        )
        node.ugly = true // todo
        node.args = emptyList()
        node.typeArgs = emptyList()
        node.lambda = null

        // todo implement all cases
        when (rootExpr.parent) {
            is Node.Stmt -> {
                val stmt = rootExpr.parent as Node.Stmt
                val block = stmt.parent as Node.Block

                val newStmts = block.stmts.toMutableList()
                val stmtIndex = newStmts.indexOf(stmt)
                newStmts.removeAt(stmtIndex)
                newStmts.add(stmtIndex, Node.Stmt.Expr(newExpr))
                block.stmts = newStmts
            }
            is Node.Decl.Property -> {
                val property = rootExpr.parent as Node.Decl.Property
                property.expr = newExpr
            }
            is Node.ValueArg -> {
                val arg = rootExpr.parent as Node.ValueArg
                arg.expr = newExpr
            }
            is Node.Decl.Func.Body.Expr -> {
                val expr = rootExpr.parent as Node.Decl.Func.Body.Expr
                expr.expr = newExpr
            }
        }
    }
}