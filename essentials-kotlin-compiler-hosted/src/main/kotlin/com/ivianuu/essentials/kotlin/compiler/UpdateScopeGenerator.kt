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

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.FunctionCodegen
import org.jetbrains.kotlin.codegen.FunctionGenerationStrategy
import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.context.ClassContext
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.load.kotlin.computeJvmDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOriginKind
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import java.io.File

fun writeUpdateScope(
    composable: FunctionDescriptor,
    c: ExpressionCodegenExtension.Context
) {
    val updateScopeDescriptor = ClassDescriptorImpl(
        composable.containingDeclaration,
        Name.identifier(composable.name.toString() + "__UpdateScope"), // todo more unuique for file + fun + params
        Modality.FINAL,
        ClassKind.CLASS,
        mutableListOf(composable.builtIns.getFunction(0).defaultType) as MutableCollection<KotlinType>,
        SourceElement.NO_SOURCE,
        false,
        LockBasedStorageManager.NO_LOCKS
    ).apply {
        initialize(
            object : MemberScopeImpl() {
                override fun printScopeStructure(p: Printer) {

                }
            },
            mutableSetOf(),
            null
        )
    }

    val classBuilderForUpdateScope = c.codegen.state.factory.newVisitor(
        JvmDeclarationOrigin(JvmDeclarationOriginKind.OTHER, null, updateScopeDescriptor),
        c.typeMapper.mapType(updateScopeDescriptor),
        mutableListOf<File>()
    )

    val classContextForUpdateScope = ClassContext(
        c.typeMapper,
        updateScopeDescriptor,
        OwnerKind.IMPLEMENTATION,
        c.codegen.context.parentContext,
        null
    )

    classBuilderForUpdateScope.defineClass(
        null,
        Opcodes.V1_6, Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL,
        c.typeMapper.mapType(updateScopeDescriptor).internalName,
        null,
        "java/lang/Object",
        emptyArray()
    )

    val functionCodegen = FunctionCodegen(
        classContextForUpdateScope,
        classBuilderForUpdateScope,
        c.codegen.parentCodegen.state,
        c.codegen.parentCodegen
    )

    writeUpdateScopeConstructor(
        c = c,
        composable = composable,
        codegen = functionCodegen,
        updateScopeClass = updateScopeDescriptor,
        asmType = c.typeMapper.mapType(updateScopeDescriptor)
    )

    writeUpdateScopeInvoke(
        c = c,
        composable = composable,
        codegen = functionCodegen,
        updateScopeClass = updateScopeDescriptor,
        asmType = Type.getType("L${c.codegen.parentCodegen.className};")
    )

    classBuilderForUpdateScope.done()
}

private fun writeUpdateScopeConstructor(
    c: ExpressionCodegenExtension.Context,
    composable: FunctionDescriptor,
    codegen: FunctionCodegen,
    updateScopeClass: ClassDescriptor,
    asmType: Type
) {
    DescriptorFactory.createPrimaryConstructorForObject(updateScopeClass, updateScopeClass.source)
        .apply {
            initialize(
                composable.valueParameters,
                Visibilities.PUBLIC
            )

            returnType = updateScopeClass.defaultType
        }
        .write(codegen) {
            v.load(0, asmType)
            v.invokespecial("java/lang/Object", "<init>", "()V", false)
            composable.valueParameters.forEachIndexed { index, param ->
                v.load(0, asmType)
                v.load(index + 1, c.typeMapper.mapType(param.type))
                v.putfield(
                    asmType.internalName,
                    param.name.toString(),
                    c.typeMapper.mapType(param.type).descriptor
                )
            }
            v.areturn(Type.VOID_TYPE)
        }
}

private fun writeUpdateScopeInvoke(
    c: ExpressionCodegenExtension.Context,
    composable: FunctionDescriptor,
    codegen: FunctionCodegen,
    updateScopeClass: ClassDescriptor,
    asmType: Type
) {
    val descriptor = object : SimpleFunctionDescriptorImpl(
        updateScopeClass,
        null,
        Annotations.EMPTY,
        Name.identifier("invoke"),
        CallableMemberDescriptor.Kind.DECLARATION,
        SourceElement.NO_SOURCE
    ) {}
    descriptor.initialize(
        null,
        null,
        mutableListOf(),
        mutableListOf(),
        composable.builtIns.unitType,
        Modality.FINAL,
        Visibilities.PUBLIC,
        null
    )

    descriptor.write(codegen) {
        /*composable.valueParameters.forEachIndexed { index, param ->
            v.load(0, asmType)
            v.getfield(
                asmType.internalName,
                param.name.toString(),
                c.typeMapper.mapType(param.type).descriptor
            )
        }

        val type = composable.containingDeclaration.fqNameSafe.asString().replace(".", "/")*/

        v.invokestatic(
            asmType.internalName,
            composable.name.toString(),
            composable.computeJvmDescriptor(withName = false),
            false
        )
        v.areturn(Type.VOID_TYPE)
    }
}

private fun FunctionDescriptor.write(codegen: FunctionCodegen, code: ExpressionCodegen.() -> Unit) {
    val declarationOrigin = JvmDeclarationOrigin(JvmDeclarationOriginKind.OTHER, null, this)
    codegen.generateMethod(
        declarationOrigin,
        this,
        object : FunctionGenerationStrategy.CodegenBased(codegen.state) {
            override fun doGenerateBody(e: ExpressionCodegen, signature: JvmMethodSignature) {
                e.code()
            }
        })
}