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

package com.ivianuu.essentials.kotlin.compiler

import com.ivianuu.essentials.kotlin.compiler.compose.ast.Node
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType

lateinit var messageCollector: MessageCollector

fun msg(block: () -> String) {
    messageCollector.report(CompilerMessageSeverity.WARNING, "essentials: ${block()}")
}

fun ClassDescriptor.asClassName() = fqNameSafe.asClassName()

fun FqName.asClassName() = ClassName.bestGuess(asString()) // todo

fun KotlinType.asTypeName(): TypeName {
    val type = constructor.declarationDescriptor!!.fqNameSafe.asClassName()
    return (if (arguments.isNotEmpty()) {
        type.parameterizedBy(*arguments.map { it.type.asTypeName() }.toTypedArray())
    } else type).copy(nullable = isMarkedNullable)
}

fun KotlinType.asType(): Node.Type {
    val pathSegments = constructor.declarationDescriptor!!.fqNameSafe.pathSegments()
    val pieces = pathSegments.mapIndexed { index, name ->
        val typeParams = if (index != pathSegments.lastIndex) {
            emptyList()
        } else {
            arguments.map { it.type.asType() }
        }
        Node.TypeRef.Simple.Piece(name.asString(), typeParams)
    }

    val typeRef = Node.TypeRef.Simple(pieces)
    return Node.Type(
        mods = emptyList(),
        ref = if (isMarkedNullable) Node.TypeRef.Nullable(typeRef) else typeRef
    )
}

fun DeclarationDescriptor.hasAnnotatedAnnotations(annotation: FqName): Boolean =
    annotations.any { it.hasAnnotation(annotation, module) }

fun AnnotationDescriptor.hasAnnotation(annotation: FqName, module: ModuleDescriptor): Boolean {
    val thisFqName = this.fqName ?: return false
    val descriptor =
        module.findClassAcrossModuleDependencies(ClassId.topLevel(thisFqName)) ?: return false
    return descriptor.annotations.hasAnnotation(annotation)
}

/*
fun report(
    descriptor: DeclarationDescriptor,
    trace: BindingTrace,
    diagnostic: (PsiElement) -> DiagnosticFactory0<PsiElement>
) {
    descriptor.findPsi()?.let {
        val factory = diagnostic(it)
        trace.reportFromPlugin(
            factory.on(it),
            OverloadComposableErrorMessages
        )
    }
}
*/