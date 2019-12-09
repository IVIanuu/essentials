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

import com.ivianuu.essentials.kotlin.compiler.compose.ast.Node
import com.ivianuu.essentials.kotlin.compiler.compose.ast.Visitor
import com.ivianuu.essentials.kotlin.compiler.compose.ast.psi.Converter
import com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.expressionVisitor
import org.jetbrains.kotlin.psi.lambdaExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.namedFunctionRecursiveVisitor
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    val funcs = mutableListOf<FuncBodyWriter>()

    val fileNode = Converter.convertFile(file)

    Visitor.visit(fileNode) { node, parent ->
        if (node !is Node.Decl.Func) return@visit
        val body = node.body as? Node.Decl.Func.Body.Block ?: return@visit
        val block = body.block
        val element = node.element as? KtNamedFunction ?: return@visit
        val descriptor =
            resolveSession.resolveToDescriptor(element) as FunctionDescriptor
        if (!descriptor.annotations.hasAnnotation(ComposableAnnotation)) return@visit
        if (descriptor.returnType?.isUnit() != true) return@visit

        val newStmts = block.stmts.toMutableList()


        block.stmts
    }

    file.accept(
        namedFunctionRecursiveVisitor { func ->


            val funcWriter =
                FuncBodyWriter(funcBody.startOffset, funcBody.endOffset, mutableListOf(), func)
            funcs += funcWriter

            funcWriter.expressions += ExprWriter("{", null)

            val funcKey = "${funcDescriptor.fqNameSafe.asString()}:${func.startOffset}"
            funcWriter.expressions += ExprWriter(
                "androidx.compose.composer.startRestartGroup(\"$funcKey\")",
                null
            )

            val exprs = mutableListOf<KtExpression>()

            fun KtElement.isInExpr(): Boolean {
                var parent: KtElement? = this
                while (parent != null) {
                    if (funcWriter.expressions.any { it.element == parent }) return true
                    parent = parent.parent as? KtElement
                }

                return false
            }

            funcBody.acceptChildren(
                lambdaExpressionRecursiveVisitor { lambda ->

                }
            )

            funcBody.acceptChildren(
                expressionVisitor { expr ->
                    exprs += expr
                    if (expr !is KtCallExpression) {
                        if (!expr.isInExpr()) {
                            funcWriter.expressions += ExprWriter(expr.text, expr)
                        }
                        return@expressionVisitor
                    }

                    val call = expr.getResolvedCall(trace.bindingContext)!!
                    val resulting = call.resultingDescriptor
                    if (!resulting.annotations.hasAnnotation(ComposableAnnotation) || resulting.returnType?.isUnit() == false) {
                        funcWriter.expressions += ExprWriter(expr.text, expr)
                        return@expressionVisitor
                    }

                    val callKey = "${funcDescriptor.fqNameSafe.asString()}:${expr.startOffset}"
                    funcWriter.expressions += ExprWriter(
                        CodeBlock.builder().apply {
                            addStatement("androidx.compose.composer.call(")
                            indent()
                            addStatement("key = \"$callKey\",")
                            addStatement("invalid = { true }")
                            addStatement("block = { ${expr.text} }")
                            unindent()
                            add(")")
                        }.build().toString(),
                        expr
                    )
                }
            )

            //error("exprs ${exprs.map { it.javaClass.toString() + " " + it.parent.javaClass + " " + it.text }.joinToString("\n\n")}")

            funcWriter.expressions += ExprWriter(
                CodeBlock.builder()
                    .beginControlFlow("androidx.compose.composer.endRestartGroup()?.updateScope {")
                    .addStatement(
                        "${funcDescriptor.name}(${funcDescriptor.valueParameters.map { it.name }.joinToString(
                            ", "
                        )})"
                    )
                    .endControlFlow()
                    .build()
                    .toString(),
                null
            )

            funcWriter.expressions += ExprWriter("}", null)
        }
    )

    /*funcs.forEach {
        error("expr in $it")
    }*/

    var currentSource = file.text
    val processedFuncs = mutableListOf<FuncBodyWriter>()
    funcs.reversed().forEach { currentFunc ->
        processedFuncs += currentFunc

        val newBody = currentFunc.bodyAsString()

        currentSource = currentSource.replaceRange(
            startIndex = currentFunc.startOffset,
            endIndex = currentFunc.endOffset,
            replacement = newBody
        )

        val oldLength = currentFunc.endOffset - currentFunc.startOffset
        val newLength = newBody.length
        val diff = when {
            newLength == oldLength -> Diff.Replaced
            newLength > oldLength -> Diff.Added
            else -> Diff.Removed
        }

        funcs
            .filter { it !in processedFuncs }
            .forEach { pendingTransform ->
                when (diff) {
                    Diff.Removed -> {
                        if (pendingTransform.startOffset < currentFunc.startOffset) {
                            pendingTransform.startOffset =
                                pendingTransform.startOffset - oldLength - newLength
                            pendingTransform.endOffset =
                                pendingTransform.endOffset - oldLength - newLength
                        }
                    }
                    Diff.Added -> {
                        if (pendingTransform.startOffset > currentFunc.startOffset) {
                            pendingTransform.startOffset =
                                pendingTransform.startOffset + newLength - oldLength
                            pendingTransform.endOffset =
                                pendingTransform.endOffset + newLength - oldLength
                        }
                    }
                    Diff.Replaced -> {
                        // do nothing
                    }
                }
            }
    }

    error("new source $currentSource")

    return null
}

enum class Diff {
    Removed, Added, Replaced
}

class FuncBodyWriter(
    var startOffset: Int,
    var endOffset: Int,
    val expressions: MutableList<ExprWriter>,
    val element: KtElement?
) {
    fun bodyAsString(): String = buildString {
        expressions.forEach {
            appendln("// start ${it.element?.name}")
            appendln(it.text)
        }
    }
}

class ExprWriter(
    val text: String,
    val element: KtElement?
)