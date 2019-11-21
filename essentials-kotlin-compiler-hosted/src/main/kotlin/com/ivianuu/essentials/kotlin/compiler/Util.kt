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

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

val COMPOSABLE_ANNOTATION = FqName("androidx.compose.Composable")
val PIVOTAL_ANNOTATION = FqName("androidx.compose.Pivotal")

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


class ComposableUpdateScopeClassDescriptor(
    composable: FunctionDescriptor,
    storageManager: StorageManager
) : ClassDescriptorImpl(
    composable.containingDeclaration,
    Name.identifier(composable.name.toString() + "__UpdateScope"), // todo more unuique
    Modality.FINAL,
    ClassKind.CLASS,
    mutableListOf(composable.builtIns.getFunction(0).defaultType) as MutableCollection<KotlinType>,
    SourceElement.NO_SOURCE,
    false,
    storageManager
)