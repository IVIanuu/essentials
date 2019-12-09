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

import com.squareup.kotlinpoet.CodeBlock
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.namedFunctionRecursiveVisitor
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.types.typeUtil.isUnit

data class Transform(
    val newText: String,
    var startOffset: Int,
    var endOffset: Int
)

class Func(
    val startOffset: Int,
    val endOffset: Int
    val expressions: List<Expr>
)

class Expr(val text: String)

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    val transforms = mutableListOf<Transform>()

    // wrap @Composables in a start restart scope
    file.accept(
        namedFunctionRecursiveVisitor { func ->
            val funcDescriptor =
                resolveSession.resolveToDescriptor(func) as FunctionDescriptor
            if (!funcDescriptor.annotations.hasAnnotation(ComposableAnnotation)) return@namedFunctionRecursiveVisitor
            if (funcDescriptor.returnType?.isUnit() != true) return@namedFunctionRecursiveVisitor
            val body = func.bodyExpression ?: return@namedFunctionRecursiveVisitor

            val bodyText = body.text
                .replaceFirst("{", "")
                .replaceLast("}", "")

            transforms += Transform(
                newText = CodeBlock.builder().apply {
                    beginControlFlow("{")
                    addStatement("androidx.compose.composer.startRestartGroup(\"todo\")")
                    unindent()
                    add(bodyText)
                    indent()
                    beginControlFlow("androidx.compose.composer.endRestartGroup()?.updateScope {")
                    addStatement(
                        "${funcDescriptor.name}(${funcDescriptor.valueParameters.map { it.name }.joinToString(
                            ", "
                        )})"
                    )
                    endControlFlow()
                    endControlFlow()
                }.build().toString(),
                startOffset = body.startOffset,
                endOffset = body.endOffset
            )
        }
    )

    file.accept(
        namedFunctionRecursiveVisitor { func ->
            val funcDescriptor =
                resolveSession.resolveToDescriptor(func) as FunctionDescriptor
            if (!funcDescriptor.annotations.hasAnnotation(ComposableAnnotation)) return@namedFunctionRecursiveVisitor
            if (funcDescriptor.returnType?.isUnit() != true) return@namedFunctionRecursiveVisitor

            func.accept(
                callExpressionRecursiveVisitor { exp ->
                    val call = exp.getResolvedCall(trace.bindingContext)!!
                    val resulting = call.resultingDescriptor
                    if (resulting.annotations.hasAnnotation(ComposableAnnotation)
                        && resulting.returnType?.isUnit() == true
                    ) {
                        transforms += Transform(
                            newText = CodeBlock.builder().apply {
                                beginControlFlow("androidx.compose.composer.call(")
                                addStatement("key = \"todo\",")
                                addStatement("invalid = { true }")
                                addStatement("block = { ${exp.text} }")
                                endControlFlow()
                                add(")")
                            }.build().toString(),
                            startOffset = exp.startOffset,
                            endOffset = exp.endOffset
                        )
                    }
                }
            )
        }
    )

    var currentSource = file.text
    val processedTransform = mutableListOf<Transform>()
    transforms.reversed().forEach { currentTransform ->
        processedTransform += currentTransform

        currentSource = currentSource.replaceRange(
            startIndex = currentTransform.startOffset,
            endIndex = currentTransform.endOffset,
            replacement = currentTransform.newText
        )

        val oldLength = currentTransform.endOffset - currentTransform.startOffset
        val newLength = currentTransform.newText.length
        val diff = when {
            newLength == oldLength -> Diff.Replaced
            newLength > oldLength -> Diff.Added
            else -> Diff.Removed
        }

        transforms
            .filter { it !in processedTransform }
            .forEach { pendingTransform ->
                when (diff) {
                    Diff.Removed -> {
                        if (pendingTransform.startOffset < currentTransform.startOffset) {
                            pendingTransform.startOffset =
                                pendingTransform.startOffset - oldLength - newLength
                            pendingTransform.endOffset =
                                pendingTransform.endOffset - oldLength - newLength
                        }
                    }
                    Diff.Added -> {
                        if (pendingTransform.startOffset > currentTransform.startOffset) {
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
