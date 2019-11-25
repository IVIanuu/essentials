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

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.MemberCodegen
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.inline.NUMBERED_FUNCTION_PREFIX
import org.jetbrains.kotlin.codegen.writeSyntheticClassMetadata
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.load.kotlin.computeJvmDescriptor
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOriginKind
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import java.io.File

fun writeUpdateScope(
    composable: FunctionDescriptor,
    codegen: MemberCodegen<*>
) {
    val state = codegen.state
    val typeMapper = codegen.typeMapper

    val asmType = composable.getUpdateScopeType()

    val classBuilderForUpdateScope = state.factory.newVisitor(
        JvmDeclarationOrigin(JvmDeclarationOriginKind.OTHER, null, null),
        asmType,
        mutableListOf<File>()
    )

    classBuilderForUpdateScope.defineClass(
        null,
        Opcodes.V1_6, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
        asmType.internalName,
        null,
        AsmTypes.LAMBDA.internalName,
        arrayOf(NUMBERED_FUNCTION_PREFIX + "0")
    )

    val receivers = mutableListOf<ReceiverParameterDescriptor>()
    composable.dispatchReceiverParameter?.let { receivers += it }
    composable.extensionReceiverParameter?.let { receivers += it }

    receivers.forEachIndexed { index, receiver ->
        classBuilderForUpdateScope.newField(
            JvmDeclarationOrigin.NO_ORIGIN,
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL or Opcodes.ACC_SYNTHETIC,
            "receiver\$$index",
            typeMapper.mapType(receiver.type).descriptor,
            null,
            null
        )
    }

    composable.valueParameters.forEach { param ->
        classBuilderForUpdateScope.newField(
            JvmDeclarationOrigin.NO_ORIGIN,
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
            param.name.asString(),
            typeMapper.mapType(param.type).descriptor,
            null,
            null
        )
    }

    classBuilderForUpdateScope.newMethod(
        JvmDeclarationOrigin.NO_ORIGIN,
        AsmUtil.NO_FLAG_PACKAGE_PRIVATE or Opcodes.ACC_SYNTHETIC,
        "<init>",
        Type.getMethodDescriptor(
            Type.VOID_TYPE,
            *(receivers.map { it.type.asmType(typeMapper) } + composable.valueParameters.map { it.type.asmType(typeMapper) }).toTypedArray()
        ), null, null
    ).apply {
        visitCode()
        InstructionAdapter(this).apply {
            receivers.forEachIndexed { index, receiver ->
                load(0, asmType)
                load(1, typeMapper.mapType(receiver.type))
                putfield(
                    asmType.internalName,
                    "receiver\$$index",
                    typeMapper.mapType(receiver.type).descriptor
                )
            }

            val skipCount = 1 + receivers.size
            composable.valueParameters.forEachIndexed { index, param ->
                load(0, asmType)
                load(index + skipCount, typeMapper.mapType(param.type))
                putfield(
                    asmType.internalName,
                    param.name.toString(),
                    typeMapper.mapType(param.type).descriptor
                )
            }

            load(0, asmType)
            iconst(0)
            invokespecial(
                AsmTypes.LAMBDA.internalName,
                "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE),
                false
            )

            areturn(Type.VOID_TYPE)
        }
        visitEnd()
    }

    classBuilderForUpdateScope.newMethod(
        JvmDeclarationOrigin.NO_ORIGIN,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_BRIDGE or Opcodes.ACC_SYNTHETIC,
        "invoke",
        "()Ljava/lang/Object;",
        null,
        null
    ).apply {
        visitCode()

        InstructionAdapter(this).apply {
            load(0, asmType)
            invokevirtual(
                asmType.internalName,
                "invoke",
                "()V",
                false
            )

            getstatic(
                "kotlin/Unit",
                "INSTANCE",
                "Lkotlin/Unit;"
            )

            areturn(asmType)
        }

        visitEnd()
    }

    classBuilderForUpdateScope.newMethod(
        JvmDeclarationOrigin.NO_ORIGIN,
        Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL,
        "invoke",
        "()V",
        null,
        null
    ).apply {
        visitCode()

        InstructionAdapter(this).apply {
            receivers.forEachIndexed { index, receiver ->
                load(0, asmType)
                getfield(
                    asmType.internalName,
                    "receiver\$$index",
                    typeMapper.mapType(receiver.type).descriptor
                )
            }

            composable.valueParameters.forEachIndexed { _, param ->
                load(0, asmType)
                getfield(
                    asmType.internalName,
                    param.name.toString(),
                    typeMapper.mapType(param.type).descriptor
                )
            }

            if (composable.dispatchReceiverParameter != null) {
                invokevirtual(
                    receivers.first().type.asmType(typeMapper).internalName,
                    composable.name.toString(),
                    Type.getMethodDescriptor(
                        Type.VOID_TYPE,
                        *(receivers.drop(1).map { it.type } + composable.valueParameters.map { it.type })
                            .map { it.asmType(typeMapper) }
                            .toTypedArray()
                    ),
                    false
                )
            } else {
                invokestatic(
                    codegen.className,
                    composable.name.toString(),
                    Type.getMethodDescriptor(
                        Type.VOID_TYPE,
                        *(receivers.map { it.type } + composable.valueParameters.map { it.type })
                            .map { it.asmType(typeMapper) }
                            .toTypedArray()
                    ),
                    false
                )
            }

            areturn(Type.VOID_TYPE)
        }

        visitEnd()
    }

    writeSyntheticClassMetadata(classBuilderForUpdateScope, state)

    classBuilderForUpdateScope.done()
}