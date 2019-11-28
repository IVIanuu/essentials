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

package com.ivianuu.essentials.kotlin.compiler.compose.overload

import com.ivianuu.essentials.kotlin.compiler.asClassName
import com.ivianuu.essentials.kotlin.compiler.asTypeName
import com.ivianuu.essentials.kotlin.compiler.compose.ComposableAnnotation
import com.ivianuu.essentials.kotlin.compiler.hasAnnotatedAnnotations
import com.ivianuu.essentials.kotlin.compiler.hasAnnotation
import com.ivianuu.essentials.kotlin.compiler.report
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeVariableName
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.isUnit
import java.io.File
import kotlin.math.absoluteValue

fun FunctionDescriptor.getOverloadComposableFileName(): String {
    val paramsHash = ((dispatchReceiverParameter?.type?.hashCode() ?: 0) +
            valueParameters.map { it.name.asString() + it.type.toString() }.hashCode()).absoluteValue
    val name = if (name.asString().startsWith("_")) {
        name.asString().removePrefix("_")
    } else name.asString()
    return "$name\$OverloadComposable\$${paramsHash}"
}

fun generateOverloadComposable(
    outputDir: File,
    descriptor: FunctionDescriptor,
    trace: BindingTrace
) {
    if (!descriptor.annotations.hasAnnotation(ComposableAnnotation)) return
    if (descriptor.source == SourceElement.NO_SOURCE) return
    if (descriptor.valueParameters.isEmpty()) return

    if (descriptor.valueParameters.none {
            it.hasAnnotatedAnnotations(OverloadComposableMarkerAnnotation)
        }) return

    val ktFile = descriptor.findPsi()!!.containingFile as KtFile

    val packageName = ktFile.packageFqName.asString()
    val fileName = descriptor.getOverloadComposableFileName()

    val name = if (descriptor.name.asString().startsWith("_")) {
        descriptor.name.asString().removePrefix("_")
    } else descriptor.name.asString()

    val imports = ktFile.importDirectives.groupBy {
        check(it.alias == null) { "Not supported yet" }// todo support
        it.importPath!!.fqName.parent().asString()
    }.mapValues {
        it.value.map { it.importPath!!.fqName.shortName().asString() }.toTypedArray()
    }

    if (!descriptor.returnType!!.isUnit()) {
        report(descriptor, trace) { MustBeUnit }
        return
    }

    val fileBuilder = FileSpec.builder(packageName, fileName)
    fileBuilder.apply {
        imports.forEach {
            addImport(it.key, *it.value)
        }
    }

    fileBuilder.addImport("androidx.compose", "Composable")

    val functionBuilder = FunSpec.builder(descriptor.name.asString())
    with(functionBuilder) {
        addAnnotation(ComposableAnnotation.asClassName())

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

        descriptor.valueParameters
            .forEach { parameter ->
                val overloads = parameter.annotations
                    .filter { annotation ->
                        annotation.hasAnnotation(
                            OverloadComposableMarkerAnnotation,
                            descriptor.module
                        )
                    }

                if (overloads.isEmpty()) {
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
                    return@forEach
                }

                if (parameter.returnType != parameter.builtIns.getFunction(0)
                    && !parameter.returnType!!.annotations.hasAnnotation(ComposableAnnotation)
                ) {
                    report(
                        parameter,
                        trace
                    ) { MustBeComposableFunction0 }
                    return
                }

                if (overloads.size > 1) {
                    report(
                        parameter,
                        trace
                    ) { OnlyOneOverloadPerParam }
                    return
                }

                val defaultValueStatement: String? = if (!parameter.declaresDefaultValue()) {
                    null
                } else if (parameter.type.isMarkedNullable) {
                    "null"
                } else {
                    report(parameter, trace) { CannotHaveDefaultValueExceptNullable }
                    return
                }

                val overload = overloads.single()

                val overloadDescriptor = descriptor.module.findClassAcrossModuleDependencies(
                    ClassId.topLevel(overload.fqName!!)
                )!!

                val overloadCompanion = overloadDescriptor.companionObjectDescriptor
                if (overloadCompanion == null) {
                    report(
                        parameter,
                        trace
                    ) { NeedsACompanionObject }
                    return
                }

                if (overloadCompanion.getSuperInterfaces().none {
                        it.fqNameSafe == OverloadComposable
                    }) {
                    report(
                        parameter,
                        trace
                    ) { CompanionObjectMustBeAOverloadComposable }
                    return
                }

                val overloadType = overloadCompanion.typeConstructor.supertypes
                    .first { it.constructor.declarationDescriptor!!.fqNameSafe == OverloadComposable }
                    .arguments
                    .single()
                    .type

                val overloadNames = parameter.annotations
                    .filter { annotation ->
                        annotation.hasAnnotation(OverloadNameAnnotation, descriptor.module)
                    }
                if (overloadNames.size > 1) {
                    report(
                        parameter,
                        trace
                    ) { OnlyOneOverloadNamePerParam }
                    return
                }
                val overloadName = overloadNames.singleOrNull()

                val finalOverloadName = overloadName?.allValueArguments
                    ?.get(Name.identifier("name"))
                    ?.value as? String
                    ?: parameter.name.asString()

                addParameter(
                    ParameterSpec.builder(
                        finalOverloadName,
                        overloadType.asTypeName()
                    )
                        .apply {
                            if (defaultValueStatement != null)
                                defaultValue(defaultValueStatement)
                        }
                        .build()
                )

                addStatement("val ${finalOverloadName}Composable: @Composable() () -> Unit = { ${overloadCompanion.fqNameSafe}.compose(${parameter.name.asString()}) }")
            }

        addStatement(
            "$name" +
                    "${descriptor.typeParameters.map { it.name.asString() }.joinToString(
                        ", "
                    ).let { if (it.isNotEmpty()) "<$it>" else "" }}" +
                    "(${descriptor.valueParameters.map { parameter ->
                        if (parameter.hasAnnotatedAnnotations(OverloadComposableMarkerAnnotation)) {
                            val overloadNames = parameter.annotations
                                .filter { annotation ->
                                    annotation.hasAnnotation(
                                        OverloadNameAnnotation,
                                        descriptor.module
                                    )
                                }
                            if (overloadNames.size > 1) {
                                report(
                                    parameter,
                                    trace
                                ) { OnlyOneOverloadNamePerParam }
                                return
                            }
                            val overloadName = overloadNames.singleOrNull()

                            val finalOverloadName = overloadName?.allValueArguments
                                ?.get(Name.identifier("name"))
                                ?.value as? String
                                ?: parameter.name.asString()

                            finalOverloadName + "Composable"
                        } else parameter.name.asString()
                    }.joinToString(
                        ", "
                    )})"
        )
    }

    fileBuilder.addFunction(functionBuilder.build())

    fileBuilder.build()
        .writeTo(outputDir)
}