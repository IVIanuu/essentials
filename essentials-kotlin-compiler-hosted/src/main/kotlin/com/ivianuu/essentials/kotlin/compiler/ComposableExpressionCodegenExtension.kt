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
import org.jetbrains.kotlin.codegen.CallGenerator
import org.jetbrains.kotlin.codegen.CallableMethod
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.codegen.generateCallReceiver
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

// todo intercept composable lambda calls
// todo wrap in start restart scope
// todo accessed functions
// todo fix keys in inline funs

class ComposableExpressionCodegenExtension : ExpressionCodegenExtension {

    override val shouldGenerateClassSyntheticPartsInLightClassesMode: Boolean
        get() = true

    override fun applyProperty(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? = apply(receiver, resolvedCall, c)

    override fun applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? = apply(receiver, resolvedCall, c)

    private fun apply(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? {
        var functionDescriptor =
            accessibleFunctionDescriptor(c.codegen, resolvedCall) ?: return null

        // todo val thisDescriptor = c.codegen.context.functionDescriptor
        // todo if (!thisDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return null

        if (!functionDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return null
        if (functionDescriptor.returnType == null) return null
        if (InlineUtil.isInline(functionDescriptor)) return null

        val superCallTarget = c.codegen.getSuperCallTarget(resolvedCall.call)

        functionDescriptor = c.codegen.context.getAccessorForSuperCallIfNeeded(
            functionDescriptor,
            superCallTarget,
            c.codegen.state
        )

        val callable = resolveToCallable(
            c.codegen,
            c.typeMapper,
            functionDescriptor,
            superCallTarget != null,
            resolvedCall
        )

        val callGenerator = getOrCreateCallGenerator(
            c.codegen,
            c.typeMapper,
            resolvedCall
        )

        val argumentGenerator = CallBasedArgumentGenerator(
            c.codegen,
            callGenerator,
            functionDescriptor.valueParameters,
            callable.valueParameterTypes
        )

        return ComposableStackValue(
            callGenerator = callGenerator,
            c = c,
            argumentGenerator = argumentGenerator,
            resolvedCall = resolvedCall,
            callable = callable as CallableMethod,
            isEffect = !functionDescriptor.returnType!!.isUnit()
        )
    }

}

private class ComposableStackValue(
    private val callGenerator: CallGenerator,
    private val c: ExpressionCodegenExtension.Context,
    private val argumentGenerator: CallBasedArgumentGenerator,
    private val resolvedCall: ResolvedCall<*>,
    private val callable: CallableMethod,
    private val isEffect: Boolean
) : StackValue(c.typeMapper.mapType(resolvedCall.candidateDescriptor.returnType!!)) {

    override fun putSelector(type: Type, kotlinType: KotlinType?, v: InstructionAdapter) {
        val invokeLabel = Label()
        val skipLabel = Label()
        val endLabel = Label()

        // store the composer
        val composerType = Type.getType("Landroidx/compose/ViewComposer;")
        val composerStoreIndex = c.codegen.frameMap.enterTemp(composerType)
        v.getComposer()
        v.store(composerStoreIndex, composerType)

        callGenerator.processAndPutHiddenParameters(false)

        val parameters = resolvedCall.valueArguments.toList()
            .sortedBy { it.first.index }
            .map { (descriptor, valueArgument) ->
                val asmType = callable.getValueParameters()[descriptor.index].asmType
                ParameterDescriptor(
                    descriptor = descriptor,
                    argument = valueArgument,
                    type = asmType,
                    storeIndex = c.codegen.frameMap.enterTemp(asmType),
                    isDefault = valueArgument is DefaultValueArgument,
                    pivotal = descriptor.annotations.hasAnnotation(PIVOTAL_ANNOTATION),
                    stable = descriptor.type.isStable()
                )
            }

        // load and store params
        val defaultArgs = argumentGenerator.generate(
            resolvedCall.valueArgumentsByIndex!!,
            resolvedCall.valueArguments.values.toList(),
            resolvedCall.resultingDescriptor
        )

        parameters.reversed()
            .forEach { param ->
                println("store $param")
                v.store(param.storeIndex, param.type)
            }

        // start group
        v.load(composerStoreIndex, composerType)

        v.invokestatic(
            "androidx/compose/sourceLocationKt",
            "sourceLocation",
            "()I",
            false
        )
        v.ensureBoxed(Type.INT_TYPE)

        val pivotals = parameters.filter { it.pivotal }
        pivotals
            .forEachIndexed { _, param ->
                val objectType = Type.getType("Ljava/lang/Object;")
                val tmpIndex = c.codegen.frameMap.enterTemp(objectType)
                v.store(tmpIndex, objectType)
                v.load(composerStoreIndex, composerType)
                v.load(tmpIndex, objectType)
                c.codegen.frameMap.leaveTemp(objectType)
                v.load(param.storeIndex, param.type)
                v.ensureBoxed(param.type)
                v.invokevirtual(
                    "androidx/compose/ViewComposer",
                    "joinKey",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
            }

        v.invokevirtual(
            "androidx/compose/ViewComposer",
            "startGroup",
            "(Ljava/lang/Object;)V",
            false
        )

        val isSkippable = !isEffect && parameters
            .filter { !it.isDefault }
            .all { it.stable }

        if (isSkippable) {
            // check changes
            parameters
                .filter { !it.isDefault }
                .forEachIndexed { index, param ->
                    v.load(composerStoreIndex, composerType)
                    v.load(param.storeIndex, param.type)
                    v.ensureBoxed(param.type)
                    v.invokevirtual(
                        "androidx/compose/ViewComposer",
                        "changed",
                        "(Ljava/lang/Object;)Z",
                        false
                    )
                    if (index != 0) v.or(Type.INT_TYPE)
                }
            if (parameters.any { !it.isDefault }) {
                v.ifne(invokeLabel)
            }

            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx/compose/ViewComposer", "getInserting", "()Z", false)
            v.ifeq(skipLabel)

            // start invocation
            v.mark(invokeLabel)
            v.load(composerStoreIndex, composerType)
            v.invokestatic(
                "androidx/compose/ViewComposerCommonKt",
                "getInvocation",
                "()Ljava/lang/Object;",
                false
            )
            v.invokevirtual(
                "androidx/compose/ViewComposer",
                "startGroup",
                "(Ljava/lang/Object;)V",
                false
            )
        }

        // push receiver
        if (resolvedCall.dispatchReceiver != null || resolvedCall.extensionReceiver != null) {
            c.codegen.generateCallReceiver(resolvedCall).put(v)
        }

        // push parameters
        parameters.forEach { param ->
            v.load(param.storeIndex, param.type)
            c.codegen.frameMap.leaveTemp(param.type)
        }

        val defaultMaskWasGenerated: Boolean =
            defaultArgs.generateOnStackIfNeeded(callGenerator, false)

        callGenerator.genCall(callable, resolvedCall, defaultMaskWasGenerated, c.codegen)

        if (isSkippable) {
            // end invocation
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx/compose/ViewComposer", "endGroup", "()V", false)
            v.goTo(endLabel)

            // skip current group
            v.mark(skipLabel)
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx/compose/ViewComposer", "skipCurrentGroup", "()V", false)

            v.mark(endLabel)
        }

        // end group
        v.load(composerStoreIndex, composerType)
        v.invokevirtual("androidx/compose/ViewComposer", "endGroup", "()V", false)
        /*if (defaultMaskWasGenerated) {
                   callable.genInvokeDefaultInstruction(v)
               } else {
                   callable.genInvokeInstruction(v)
               }*/
        c.codegen.frameMap.leaveTemp(composerType)
    }
}

private data class ParameterDescriptor(
    val descriptor: ValueParameterDescriptor,
    val argument: ResolvedValueArgument,
    val type: Type,
    val storeIndex: Int,
    val isDefault: Boolean,
    val pivotal: Boolean,
    val stable: Boolean
)

private fun InstructionAdapter.ensureBoxed(type: Type) {
    when (type) {
        Type.INT_TYPE -> {
            invokestatic(
                "java/lang/Integer",
                "valueOf",
                "(I)Ljava/lang/Integer;",
                false
            )
        }
        Type.BOOLEAN_TYPE -> {
            invokestatic(
                "java/lang/Boolean",
                "valueOf",
                "(Z)Ljava/lang/Boolean;",
                false
            )
        }
        Type.CHAR_TYPE -> {
            invokestatic(
                "java/lang/Character",
                "valueOf",
                "(C)Ljava/lang/Character;",
                false
            )
        }
        Type.SHORT_TYPE -> {
            invokestatic("java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false)
        }
        Type.LONG_TYPE -> {
            invokestatic("java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false)
        }
        Type.BYTE_TYPE -> {
            invokestatic("java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false)
        }
        Type.FLOAT_TYPE -> {
            invokestatic("java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false)
        }
        Type.DOUBLE_TYPE -> {
            invokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false)
        }
    }
}