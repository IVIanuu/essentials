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

import com.ivianuu.essentials.kotlin.compiler.asClassName
import com.ivianuu.essentials.kotlin.compiler.asTypeName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isUnit
import java.io.File
import kotlin.math.absoluteValue

fun FunctionDescriptor.getUpdateScopeName(): String {
    val paramsHash = ((dispatchReceiverParameter?.type?.hashCode() ?: 0) +
            valueParameters.map { it.name.asString() + it.type.toString() }.hashCode()).absoluteValue
    return "${name.asString()}__Composable\$${paramsHash}"
}

fun generateComposableWrapper(
    outputDir: File,
    descriptor: FunctionDescriptor,
    trace: BindingTrace
) {
    if (!descriptor.annotations.hasAnnotation(ComposableAnnotation)) return
    if (descriptor.source == SourceElement.NO_SOURCE) return
    if (!descriptor.name.asString().startsWith("_")) return

    val ktFile = descriptor.findPsi()!!.containingFile as KtFile

    val packageName = ktFile.packageFqName.asString()
    val fileName = descriptor.getUpdateScopeName()

    val imports = ktFile.importDirectives.groupBy {
        check(it.alias == null) { "Not supported yet" }// todo support
        it.importPath!!.fqName.parent().asString()
    }.mapValues {
        it.value.map { it.importPath!!.fqName.shortName().asString() }.toTypedArray()
    }

    val isEffect = !descriptor.returnType!!.isUnit()

    FileSpec.builder(packageName, fileName)
        .apply {
            imports.forEach {
                addImport(it.key, *it.value)
            }
        }
        .addImport(
            "com.ivianuu.essentials.ui.compose.core",
            "composable",
            "staticComposable",
            "effect"
        )
        .apply {
            FunSpec.builder(descriptor.name.asString().replaceFirst("_", ""))
                .addAnnotation(ComposableAnnotation.asClassName())
                .addModifiers(KModifier.INLINE)
                .apply {
                    descriptor.typeParameters.forEach { typeParameter ->
                        addTypeVariable(
                            TypeVariableName(
                                name = typeParameter.name.asString(),
                                bounds = *typeParameter.upperBounds.map { it.asTypeName() }.toTypedArray(),
                                variance = when (typeParameter.variance) {
                                    Variance.INVARIANT -> null
                                    Variance.IN_VARIANCE -> KModifier.IN
                                    Variance.OUT_VARIANCE -> KModifier.OUT
                                }
                            )
                        )
                    }
                }
                .apply {
                    descriptor.valueParameters.forEach { parameter ->
                        addParameter(
                            ParameterSpec.builder(
                                parameter.name.asString(),
                                parameter.type.asTypeName()
                            )
                                .apply {
                                    if (parameter.declaresDefaultValue()) {
                                        val ktParameter = parameter.findPsi() as KtParameter
                                        defaultValue(ktParameter.defaultValue!!.text)
                                    }
                                }
                                .build()
                        )
                    }
                }
                .apply {
                    if (isEffect) {
                        returns(descriptor.returnType!!.asTypeName())
                    }
                }
                .apply {
                    addCode(
                        CodeBlock.builder()
                            .apply {
                                if (isEffect) {
                                    beginControlFlow("return effect")
                                } else {
                                    when {
                                        descriptor.valueParameters.any { it.type.isStable() } -> {
                                            beginControlFlow(
                                                "composable(${descriptor.valueParameters.map { it.name.asString() }.joinToString(
                                                    ", "
                                                )})"
                                            )
                                        }
                                        descriptor.valueParameters.isEmpty() -> {
                                            beginControlFlow("staticComposable")
                                        }
                                        else -> {
                                            beginControlFlow("composable")
                                        }
                                    }
                                }

                                add("${descriptor.name}" +
                                        "${descriptor.typeParameters.map { it.name.asString() }.joinToString(
                                            ", "
                                        ).let { if (it.isNotEmpty()) "<$it>" else "" }}" +
                                        "(${descriptor.valueParameters.map { it.name.asString() }.joinToString(
                                            ", "
                                        )})"
                                )
                                endControlFlow()
                            }
                            .build()
                    )
                }
                .build()
                .let { addFunction(it) }
        }
        .build()
        .writeTo(outputDir)

}