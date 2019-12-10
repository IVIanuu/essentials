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

import com.ivianuu.essentials.kotlin.compiler.compose.ast.Converter
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Node
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Visitor
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Writer
import com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    val fileNode = Converter().convertFile(file)

    /*val allNodes = mutableListOf<Node?>()
    Visitor.visit(fileNode) { node, _ ->
        allNodes += node
    }

    error("all nodes $allNodes")'*/

    insertRestartScope(fileNode, resolveSession, trace)

    wrapComposableCalls(fileNode, resolveSession, trace)
    wrapEffectCalls(fileNode, resolveSession, trace)

    val newSource = Writer.write(fileNode)

    error("new source $newSource")

    return file.withNewSource(newSource)
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
            if (!resulting.annotations.hasAnnotation(ComposableAnnotation) || resulting.returnType?.isUnit() == false) {
                return@visit
            }

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
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) || resulting.returnType?.isUnit() == false) {
            return@visit
        }

        val funcDescriptor = resolveSession.resolveToDescriptor(func!!.element!! as KtNamedFunction)

        val callKey = "${funcDescriptor.fqNameSafe.asString()}:${element.startOffset}".hashCode()
        val callKeyIndex = nextKeyIndex()

        node.expr = Node.Expr.Text(
            CodeBlock.builder().apply {
                node.args.forEachIndexed { index, valueArg ->
                    addStatement("val arg_${callKeyIndex}_${index} = ${valueArg.element!!.text}")
                }

                addStatement("var key_$callKeyIndex: Any = $callKey")

                resulting.valueParameters.forEachIndexed { index, param ->
                    if (param.annotations.hasAnnotation(PivotalAnnotation)) {
                        addStatement("key_$callKeyIndex = compose_composer.joinKey(key_$callKeyIndex, arg_${callKeyIndex}_${index})")
                    }
                }

                addStatement("compose_composer.call(")
                indent()
                addStatement("key = key_$callKeyIndex,")
                addStatement("invalid = {")
                indent()

                if (resulting.valueParameters.isEmpty()) {
                    addStatement("false")
                } else {
                    val stableParams = resulting.valueParameters
                        .filter { it.type.isStable() }

                    if (stableParams.size != resulting.valueParameters.size) {
                        addStatement("true")
                    } else {
                        addStatement(
                            stableParams.joinToString(" or ") {
                                "changed(arg_${callKeyIndex}_${it.index})"
                            }
                        )
                    }
                }
                unindent()
                addStatement("},")
                addStatement(
                    "block = { ${node.expr.element!!.text}(${node.args.indices.joinToString(
                        ", "
                    ) { "arg_${callKeyIndex}_${it}" }}) }")
                unindent()
                add(")")
            }.build().toString()
        )

        node.ugly = true
        node.args = emptyList()
        node.typeArgs = emptyList()
        node.lambda = null
    }
}

private fun wrapEffectCalls(
    file: Node.File,
    resolveSession: ResolveSession,
    trace: BindingTrace
) {
    var func: Node.Decl.Func? = null
    var keyIndex = 0
    fun nextKeyIndex() = ++keyIndex
    Visitor.visit(file) { node, _ ->
        if (node is Node.Decl.Func) {
            func = node
            keyIndex = 0
        }
        if (node !is Node.Expr.Call) return@visit
        if (func == null) return@visit
        val element = node.element as? KtCallExpression ?: return@visit
        val resolvedCall = element.getResolvedCall(trace.bindingContext)!!
        val resulting = resolvedCall.resultingDescriptor
        if (!resulting.annotations.hasAnnotation(ComposableAnnotation) || resulting.returnType?.isUnit() == true) {
            return@visit
        }

        val funcDescriptor = resolveSession.resolveToDescriptor(func!!.element!! as KtNamedFunction)

        val callKey = "${funcDescriptor.fqNameSafe.asString()}:${element.startOffset}".hashCode()
        val callKeyIndex = nextKeyIndex()

        error("parents ${node.parent!!.parent!!.parent!!.parent}")

        // direct assignment
        var (propertyAssigmentName, stmtIndex) = if (node.parent is Node.Decl.Property) {
            if (node.parent!!.parent is Node.Stmt.Decl) {
                if (node.parent!!.parent!!.parent is Node.Block) {
                    val block = node.parent!!.parent!!.parent as Node.Block
                    val stmtToRemove = node.parent!!.parent as Node.Stmt.Decl
                    val index = block.stmts.indexOf(stmtToRemove)
                    block.stmts = block.stmts.toMutableList().also {
                        it.remove(stmtToRemove)
                    }

                    val property = node.parent as Node.Decl.Property
                    property.vars.first()?.name to index
                } else null to null
            } else null to null
        } else null to null

        if (node.parent is Node.Expr.BinaryOp) {
            if (node.parent!!.parent is Node.Stmt.Decl) {
                if (node.parent!!.parent!!.parent is Node.Block) {
                    val block = node.parent!!.parent!!.parent as Node.Block
                    val stmtToRemove = node.parent!!.parent as Node.Stmt.Decl
                    val index = block.stmts.indexOf(stmtToRemove)
                    block.stmts = block.stmts.toMutableList().also {
                        it.remove(stmtToRemove)
                    }

                    val property = node.parent as Node.Decl.Property
                    property.vars.first()?.name to index
                } else null to null
            } else null to null
        } else null to null

        val effectExpr = Node.Expr.Text(
            CodeBlock.builder().apply {
                node.args.forEachIndexed { index, valueArg ->
                    addStatement("val arg_${callKeyIndex}_${index} = ${valueArg.element!!.text}")
                }

                addStatement("var key_$callKeyIndex: Any = $callKey")

                resulting.valueParameters.forEachIndexed { index, param ->
                    if (param.annotations.hasAnnotation(PivotalAnnotation)) {
                        addStatement("key_$callKeyIndex = compose_composer.joinKey(key_$callKeyIndex, arg_${callKeyIndex}_${index})")
                    }
                }

                if (propertyAssigmentName != null) {
                    addStatement("var $propertyAssigmentName = compose_composer.expr(")
                } else {
                    addStatement("compose_composer.expr(")
                }

                indent()
                addStatement("key = key_$callKeyIndex,")
                addStatement(
                    "block = { ${node.expr.element!!.text}(${node.args.indices.joinToString(
                        ", "
                    ) { "arg_${callKeyIndex}_${it}" }}) }"
                )
                unindent()
                add(")")
            }.build().toString()
        )

        if (propertyAssigmentName == null) {
            node.expr = effectExpr
            node.ugly = true
            node.args = emptyList()
            node.typeArgs = emptyList()
            node.lambda = null
        } else {
            val block = node.parent!!.parent!!.parent as Node.Block
            block.stmts = block.stmts.toMutableList().also {
                it.add(stmtIndex!!, Node.Stmt.Expr(effectExpr))
            }
        }
    }
}

/**
private fun splitComposablePropertyDeclAndAssignment(
file: Node.File,
resolveSession: ResolveSession,
trace: BindingTrace
) {
var func: Node.Decl.Func? = null
var block: Node.Block? = null

Visitor.visit(file) { node, parent ->
if (node is Node.Decl.Func) {
func = node
}
if (node is Node.Block) {
block = node
}
if (node !is Node.Stmt.Decl) return@visit
if (func == null) return@visit
if (block == null) return@visit
val decl = node.decl
if (decl !is Node.Decl.Property) return@visit
val property = decl.element as KtProperty
//val propertyDescriptor = resolveSession.resolveToDescriptor(property) as PropertyDescriptor
val stmts = block!!.stmts
val stmtIndex = stmts.indexOf(node)
if (stmtIndex == -1) return@visit
val call = decl.expr as? Node.Expr.Call ?: return@visit
val callExpression = call.element as? KtCallExpression ?: return@visit
val resolvedCall = callExpression.getResolvedCall(trace.bindingContext)!!
val resulting = resolvedCall.resultingDescriptor
if (!resulting.annotations.hasAnnotation(ComposableAnnotation)) {
return@visit
}

val newStmts = block!!.stmts.toMutableList()
val declStatement = Node.Stmt.Expr(
Node.Expr.Text("var ${property.name}: ${resulting.returnType}")
)
newStmts.set(stmtIndex, declStatement)

val assignmentStmt = Node.Stmt.Expr(
Node.Expr.BinaryOp(
lhs = Node.Expr.Text("${property.name}"),
oper = Node.Expr.BinaryOp.Oper.Token(Node.Expr.BinaryOp.Token.ASSN),
rhs = call
)
)

newStmts.add(stmtIndex + 1, assignmentStmt)
block!!.stmts = newStmts

// error("composable assignment stmt index $stmtIndex")

}
}*/