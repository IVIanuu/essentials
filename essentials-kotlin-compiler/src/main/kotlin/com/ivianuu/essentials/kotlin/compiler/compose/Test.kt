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
import org.jetbrains.kotlin.psi.namedFunctionRecursiveVisitor
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.ResolveSession

data class Transform(
    val newText: String,
    var startOffset: Int,
    var endOffset: Int
)

fun String.replaceLast(oldValue: String, newValue: String, ignoreCase: Boolean = false): String {
    val index = lastIndexOf(oldValue, ignoreCase = ignoreCase)
    return if (index < 0) this else this.replaceRange(index, index + oldValue.length, newValue)
}

class Escape : RuntimeException()

private const val PROCESSED_MARKER = "/*=:=processed=:=*/"

fun test(
    trace: BindingTrace,
    resolveSession: ResolveSession,
    file: KtFile
): KtFile? {
    var transform: Transform? = null
    try {
        fun setTransform(t: Transform) {
            transform = t
            throw Escape()
        }
        file.accept(
            namedFunctionRecursiveVisitor { func ->
                val funcDescriptor =
                    resolveSession.resolveToDescriptor(func) as FunctionDescriptor
                if (funcDescriptor.annotations.hasAnnotation(ComposableAnnotation)) {
                    val body = func.bodyExpression
                    if (body != null) {
                        if (body.text.contains(PROCESSED_MARKER)) return@namedFunctionRecursiveVisitor

                        val bodyText = body.text
                            .replaceFirst("{", "")
                            .replaceLast("}", "")
                        setTransform(
                            Transform(
                                newText = CodeBlock.builder().apply {
                                    beginControlFlow("{")
                                    addStatement(PROCESSED_MARKER)
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
                        )
                    }

                    /*func.accept(
                        callExpressionRecursiveVisitor { exp ->
                            val call = exp.getResolvedCall(trace.bindingContext)!!
                            val resulting = call.resultingDescriptor
                            if (resulting.annotations.hasAnnotation(ComposableAnnotation)) {
                                transforms += Transform(
                                    newText = CodeBlock.builder().apply {
                                        addStatement("println(\"pre invocation ${resulting.name}\")")
                                        unindent()
                                        add(exp.text)
                                        indent()
                                        addStatement("println(\"post invocation ${resulting.name}\")")
                                    }.build().toString(),
                                    startOffset = exp.startOffset,
                                    endOffset = exp.endOffset
                                )
                            } else {
                                transforms += Transform(
                                    newText = "",
                                    startOffset = exp.startOffset,
                                    endOffset = exp.endOffset
                                )
                            }
                        }
                    )*/
                }
            }
        )
    } catch (e: Escape) {
    }

    return if (transform != null) {
        val newSource = file.text.replaceRange(
            transform!!.startOffset,
            transform!!.endOffset,
            transform!!.newText
        )
        file.withNewSource(newSource)
    } else return null
}
