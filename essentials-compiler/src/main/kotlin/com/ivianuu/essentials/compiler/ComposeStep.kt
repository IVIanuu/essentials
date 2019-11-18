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

package com.ivianuu.essentials.compiler

import androidx.compose.Composable
import androidx.compose.Pivotal
import com.google.common.collect.SetMultimap
import com.ivianuu.processingx.filer
import com.ivianuu.processingx.getPackage
import com.ivianuu.processingx.hasAnnotation
import com.ivianuu.processingx.javaToKotlinType
import com.ivianuu.processingx.steps.ProcessingStep
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import kotlin.reflect.KClass

class ComposeStep : ProcessingStep() {

    override fun annotations() = setOf(Composable::class)

    override fun process(elementsByAnnotation: SetMultimap<KClass<out Annotation>, Element>): Set<Element> {
        elementsByAnnotation[Composable::class]
            .filterIsInstance<ExecutableElement>()
            .mapNotNull { createDescriptor(it) }
            .map { ComposeGenerator(it) }
            .map { it.generate() }
            .forEach { it.writeTo(filer) }

        return emptySet()
    }

    private fun createDescriptor(element: ExecutableElement): ComposableDescriptor? {
        val packageName = element.getPackage().qualifiedName.toString()

        if (!element.simpleName.toString().startsWith("_")) {
            return null
        }

        val originalComposableName = element.simpleName.toString()
        val composableName = originalComposableName.removePrefix("_")
        val className = element.enclosingElement.simpleName.toString()

        val parameters = element.parameters
            .map { param ->
                ComposableParamDescriptor(
                    typeName = param.asType().asTypeName().javaToKotlinType() as ClassName,
                    parameterName = param.simpleName.toString(),
                    isPivotal = param.hasAnnotation<Pivotal>()
                )
            }

        val composableKey = (packageName +
                className +
                composableName +
                parameters.joinToString("") { it.parameterName + it.typeName.canonicalName })
            .hashCode()

        return ComposableDescriptor(
            packageName = packageName,
            fileName = composableName + "__$composableKey",
            composableKey = composableKey,
            composableName = composableName,
            originalComposableName = originalComposableName,
            parameters = parameters
        )
    }
}

class ComposeGenerator(private val descriptor: ComposableDescriptor) {

    fun generate(): FileSpec {
        return FileSpec.builder(descriptor.packageName, descriptor.fileName)
            .addImport("androidx.compose", "composer")
            .addFunction(composableFunction())
            .build()
    }

    private fun composableFunction() = FunSpec.builder(descriptor.composableName)
        .addAnnotation(Composable::class)
        .apply {
            descriptor.parameters.forEach { param ->
                addParameter(param.parameterName, param.typeName)
            }
        }
        .addCode(
            CodeBlock.builder()
                .apply {
                    addStatement("with(composer.composer) {")
                    indent()

                    // todo use pivotal params
                    if (descriptor.parameters.any { it.isPivotal }) {
                        addStatement(
                            "val key = \"${descriptor.composableKey}${descriptor.parameters.filter { it.isPivotal }.joinToString(
                                ""
                            ) { "\${${it.parameterName}.hashCode()}" }}\""
                        )
                    } else {
                        addStatement("val key = ${descriptor.composableKey}")
                    }

                    addStatement("startRestartGroup(key)")

                    if (descriptor.parameters.size == 1) {
                        beginControlFlow("if (changed(${descriptor.parameters.first().parameterName}) || inserting)")
                    } else {
                        beginControlFlow("if (changed(listOf(${descriptor.parameters.joinToString(",") { it.parameterName }})) || inserting)")
                    }
                    addStatement("startGroup(key)")
                    addStatement(
                        "${descriptor.originalComposableName}(${descriptor.parameters.joinToString(
                            ","
                        ) { it.parameterName }})"
                    )
                    addStatement("endGroup()")
                    nextControlFlow("else")
                    addStatement("skipCurrentGroup()")
                    endControlFlow()

                    addStatement(
                        "endRestartGroup()?.updateScope { ${descriptor.composableName}(${descriptor.parameters.joinToString(
                            ","
                        ) { it.parameterName }}) }"
                    )
                    unindent()
                    addStatement("}")
                }
                .build()
        )
        .build()
}

data class ComposableDescriptor(
    val packageName: String,
    val fileName: String,
    val composableKey: Int,
    val composableName: String,
    val originalComposableName: String,
    val parameters: List<ComposableParamDescriptor>
)

data class ComposableParamDescriptor(
    val typeName: ClassName,
    val parameterName: String,
    val isPivotal: Boolean
)