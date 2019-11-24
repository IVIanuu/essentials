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

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils.NO_EXPECTED_TYPE
import org.jetbrains.kotlin.types.TypeUtils.UNIT_EXPECTED_TYPE
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

val COMPOSABLE_ANNOTATION = FqName("androidx.compose.Composable")
val PIVOTAL_ANNOTATION = FqName("androidx.compose.Pivotal")
val STABLE_MARKER_ANNOTATION = FqName("androidx.compose.StableMarker")

fun KotlinType.isStable(): Boolean {
    return !isError &&
            !isTypeParameter() &&
            !isSpecialType &&
            (
                    KotlinBuiltIns.isPrimitiveType(this) ||
                            isFunctionType ||
                            isEnum() ||
                            isMarkedStable() ||
                            (isNullable() && makeNotNullable().isStable()))
}

private val KotlinType.isSpecialType: Boolean
    get() =
        this === NO_EXPECTED_TYPE || this === UNIT_EXPECTED_TYPE

private fun KotlinType.isMarkedStable(): Boolean =
    !isSpecialType && (
            annotations.hasStableMarker() ||
                    (constructor.declarationDescriptor?.annotations?.hasStableMarker() ?: false))

private fun Annotations.hasStableMarker(): Boolean = any { it.isStableMarker() }

private fun AnnotationDescriptor.isStableMarker(): Boolean {
    val classDescriptor = annotationClass ?: return false
    return classDescriptor.annotations.hasAnnotation(STABLE_MARKER_ANNOTATION)
}

fun InstructionAdapter.getComposer() {
    invokestatic(
        "androidx/compose/ViewComposerKt",
        "getComposer",
        "()Landroidx/compose/ViewComposition;",
        false
    )
    invokevirtual(
        "androidx/compose/ViewComposition",
        "getComposer",
        "()Landroidx/compose/ViewComposer;",
        false
    )
}