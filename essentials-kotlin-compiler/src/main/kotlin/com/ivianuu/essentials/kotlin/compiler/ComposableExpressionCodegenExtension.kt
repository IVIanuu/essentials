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
import org.jetbrains.kotlin.codegen.MemberCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.codegen.generateCallReceiver
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

// todo intercept composable lambda calls
// todo wrap in start restart scope
// todo property support

class ComposableExpressionCodegenExtension : ExpressionCodegenExtension {

    override val shouldGenerateClassSyntheticPartsInLightClassesMode: Boolean
        get() = true

    override fun applyProperty(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? = null // todo apply(receiver, resolvedCall, c)

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
        ) as CallableMethod

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

        val isEffect = !functionDescriptor.returnType!!.isUnit()

        return StackValue.functionCall(
            callable.returnType,
            functionDescriptor.original.returnType
        ) { v ->
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
            val objectType = Type.getType("Ljava/lang/Object;")

            StackValue.coerce(Type.INT_TYPE, objectType, v)

            val pivotals = parameters.filter { it.pivotal }
            pivotals
                .forEachIndexed { _, param ->
                    val tmpIndex = c.codegen.frameMap.enterTemp(objectType)
                    v.store(tmpIndex, objectType)
                    v.load(composerStoreIndex, composerType)
                    v.load(tmpIndex, objectType)
                    c.codegen.frameMap.leaveTemp(objectType)
                    v.load(param.storeIndex, param.type)
                    StackValue.coerce(param.type, objectType, v)
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
                        StackValue.coerce(param.type, objectType, v)
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

            c.codegen.frameMap.leaveTemp(composerType)
        }
    }

    override fun onEnterFunctionBody(
        descriptor: FunctionDescriptor,
        v: InstructionAdapter,
        parentCodegen: MemberCodegen<*>
    ) {
        if (!descriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return
        if (!descriptor.returnType!!.isUnit()) return

        writeUpdateScope(descriptor, parentCodegen)

        v.getComposer()
        v.iconst(descriptor.fqNameSafe.hashCode() /* xor lineNumber*/) // todo
        val objectType = Type.getType("Ljava/lang/Object;")
        StackValue.coerce(Type.INT_TYPE, objectType, v)
        v.invokevirtual(
            "androidx/compose/ViewComposer",
            "startRestartGroup",
            "(Ljava/lang/Object;)V",
            false
        )
    }

    override fun onExitFunctionBody(
        descriptor: FunctionDescriptor,
        v: InstructionAdapter,
        parentCodegen: MemberCodegen<*>
    ) {
        if (!descriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return
        if (!descriptor.returnType!!.isUnit()) return
        v.getComposer()
        v.invokevirtual(
            "androidx/compose/ViewComposer",
            "endRestartGroup",
            "()Landroidx/compose/ScopeUpdateScope;",
            false
        )
        v.dup()
        val returnLabel = Label()
        val nullLabel = Label()
        v.ifnull(nullLabel)
        val updateScopeName = "${descriptor.fqNameSafe.asString().replace(".", "/")}__UpdateScope"
        v.anew(Type.getType("L$updateScopeName;"))
        v.dup()
        descriptor.valueParameters.forEach {
            v.load(it.index, parentCodegen.typeMapper.mapType(it.type))
        }
        v.invokespecial(
            updateScopeName,
            "<init>",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                *descriptor.valueParameters
                    .map { parentCodegen.typeMapper.mapType(it.type) }
                    .toTypedArray()
            ),
            false
        )
        v.checkcast(Type.getType("Lkotlin/jvm/functions/Function0;"))
        v.invokeinterface(
            "androidx/compose/ScopeUpdateScope",
            "updateScope",
            "(Lkotlin/jvm/functions/Function0;)V"
        )
        v.goTo(returnLabel)

        v.mark(nullLabel)
        v.pop()

        v.mark(returnLabel)
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