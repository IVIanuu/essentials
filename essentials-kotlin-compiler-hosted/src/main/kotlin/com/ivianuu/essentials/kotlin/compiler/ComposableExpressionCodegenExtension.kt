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

import org.jetbrains.kotlin.codegen.CallBasedArgumentGenerator
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.SamCodegenUtil
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.codegen.generateCallReceiver
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.getSuperCallExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ComposableExpressionCodegenExtension : ExpressionCodegenExtension {

    override val shouldGenerateClassSyntheticPartsInLightClassesMode: Boolean
        get() = true

    override fun applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? {
        val candidate = resolvedCall.candidateDescriptor

        if (!candidate.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) {
            return null
        }

        if (resolvedCall.resultingDescriptor !is FunctionDescriptor) return null

        val superCallTarget = getSuperCallExpression(resolvedCall.call)
        val callable = c.typeMapper.mapToCallableMethod(
            SamCodegenUtil.resolveSamAdapter(resolvedCall.resultingDescriptor as FunctionDescriptor),
            superCallTarget != null,
            null,
            resolvedCall
        )

        val callGenerator = c.codegen.defaultCallGenerator
        val descriptor = resolvedCall.resultingDescriptor

        val argumentGenerator = CallBasedArgumentGenerator(
            c.codegen, callGenerator, descriptor.valueParameters,
            callable.valueParameterTypes
        )

        return ComposableStackValue(
            c = c,
            argumentGenerator = argumentGenerator,
            resolvedCall = resolvedCall,
            callable = callable
        )
    }

    private class ComposableStackValue(
        private val c: ExpressionCodegenExtension.Context,
        private val argumentGenerator: CallBasedArgumentGenerator,
        private val resolvedCall: ResolvedCall<*>,
        private val callable: CallableMethod
    ) : StackValue(Type.VOID_TYPE) {
        override fun putSelector(type: Type, kotlinType: KotlinType?, v: InstructionAdapter) {
            // store the composer
            val composerType = Type.getType("Landroidx/compose/ViewComposer;")
            val composerStoreIndex = c.codegen.frameMap.enterTemp(composerType)
            v.invokestatic(
                "androidx/compose.ViewComposerKt",
                "getComposer",
                "()Landroidx/compose/ViewComposition",
                false
            )
            v.invokevirtual(
                "androidx.compose.ViewComposition",
                "getComposer",
                "()Landroidx/compose/ViewComposer",
                false
            )
            v.store(composerStoreIndex, composerType)

            // start group
            v.load(composerStoreIndex, composerType)
            v.invokestatic(
                "com/ivianuu/essentials/util/SourceLocationKt",
                "sourceLocation",
                "()Ljava/lang/Object",
                false
            )
            v.invokevirtual(
                "androidx.composer.ViewComposer",
                "startGroup",
                "(Ljava/lang/Object;)V",
                false
            )

            // load arguments on top of the stack
            argumentGenerator.generate(
                resolvedCall.valueArgumentsByIndex!!,
                resolvedCall.valueArguments.values.toList(),
                resolvedCall.resultingDescriptor
            )

            val parameters = resolvedCall.valueArguments.toList()
                .map { it.first }
                .sortedBy { it.index }

            val typeByParam = parameters.associateWith {
                callable.getValueParameters()[it.index].asmType
            }
            val storeIndexByParam = parameters.associateWith {
                c.codegen.frameMap.enterTemp(typeByParam.getValue(it))
            }

            // store each argument
            parameters.reversed().forEachIndexed { index, descriptor ->
                v.store(storeIndexByParam.getValue(descriptor), typeByParam.getValue(descriptor))
            }

            val invokeLabel = Label()
            val skipLabel = Label()
            val endLabel = Label()

            parameters.forEach {
                v.load(composerStoreIndex, composerType)
                v.load(storeIndexByParam.getValue(it), typeByParam.getValue(it))
                v.ensureBoxed(typeByParam.getValue(it))
                v.invokevirtual(
                    "androidx/compose/ViewComposer",
                    "changed",
                    "(Ljava/lang/Object;)Z",
                    false
                )
                v.ifne(invokeLabel)
            }

            if (parameters.isNotEmpty()) {
                v.load(composerStoreIndex, composerType)
                v.invokevirtual("androidx/compose/ViewComposer", "getInserting", "()Z", false)
                v.ifeq(skipLabel)
            }

            // start invocation
            v.mark(invokeLabel)
            v.load(composerStoreIndex, composerType)
            v.invokestatic(
                "androidx/compose/ViewComposerCommonKt",
                "getInvocation",
                "()Ljava/lang/Object",
                false
            )
            v.invokevirtual(
                "androidx.composer.ViewComposer",
                "startGroup",
                "(Ljava/lang/Object;)V",
                false
            )

            if (resolvedCall.dispatchReceiver != null || resolvedCall.extensionReceiver != null) {
                c.codegen.generateCallReceiver(resolvedCall).put(v)
            }

            println("parameters ${parameters.map { it.name to it.type }} callable ${callable.getValueParameters().map { it.asmType }}")

            // push parameters
            parameters.forEachIndexed { index, descriptor ->
                v.load(storeIndexByParam.getValue(descriptor), typeByParam.getValue(descriptor))
                c.codegen.frameMap.leaveTemp(typeByParam.getValue(descriptor))
            }
            callable.genInvokeInstruction(v)

            // end invocation
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx.composer.ViewComposer", "endGroup", "()V", false)
            v.goTo(endLabel)

            // skip current group
            v.mark(skipLabel)
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx.composer.ViewComposer", "skipCurrentGroup", "()V", false)

            // end group
            v.mark(endLabel)
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx.composer.ViewComposer", "endGroup", "()V", false)

            c.codegen.frameMap.leaveTemp(composerType)
        }

        private fun InstructionAdapter.ensureBoxed(type: Type) {
            when (type) {
                Type.INT_TYPE -> {
                    invokestatic(
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer",
                        false
                    )
                }
                Type.BOOLEAN_TYPE -> {
                    invokestatic(
                        "java/lang/Boolean",
                        "valueOf",
                        "(Z)Ljava/lang/Boolean",
                        false
                    )
                }
                Type.CHAR_TYPE -> {
                    invokestatic(
                        "java/lang/Character",
                        "valueOf",
                        "(C)Ljava/lang/Character",
                        false
                    )
                }
                Type.SHORT_TYPE -> {
                    invokestatic("java/lang/Short", "valueOf", "(S)Ljava/lang/Short", false)
                }
                Type.LONG_TYPE -> {
                    invokestatic("java/lang/Long", "valueOf", "(J)Ljava/lang/Long", false)
                }
                Type.BYTE_TYPE -> {
                    invokestatic("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte", false)
                }
                Type.FLOAT_TYPE -> {
                    invokestatic("java/lang/Float", "valueOf", "(F)Ljava/lang/Float", false)
                }
                Type.DOUBLE_TYPE -> {
                    invokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double", false)
                }
            }
        }
    }

}

private val COMPOSABLE_ANNOTATION = FqName("androidx.compose.Composable")