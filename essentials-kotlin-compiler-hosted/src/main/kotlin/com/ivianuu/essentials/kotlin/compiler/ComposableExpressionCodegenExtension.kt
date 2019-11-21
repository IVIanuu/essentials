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
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.FunctionCodegen
import org.jetbrains.kotlin.codegen.FunctionGenerationStrategy
import org.jetbrains.kotlin.codegen.OwnerKind
import org.jetbrains.kotlin.codegen.SamCodegenUtil
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.context.ClassContext
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.codegen.generateCallReceiver
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.load.kotlin.computeJvmDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.getSuperCallExpression
import org.jetbrains.kotlin.resolve.calls.model.DefaultValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOriginKind
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import java.io.File

// todo intercept composable lambda calls

// todo wrap in start restart scope

// todo move source location gen to this class

class ComposableExpressionCodegenExtension : ExpressionCodegenExtension {

    override val shouldGenerateClassSyntheticPartsInLightClassesMode: Boolean
        get() = true

    override fun applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
    ): StackValue? {
        val thisDescriptor = c.codegen.context.functionDescriptor
        if (!thisDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return null
        // todo writeUpdateScope(thisDescriptor, c)

        val resultingDescriptor = resolvedCall.resultingDescriptor
        if (resultingDescriptor !is FunctionDescriptor) return null
        if (!resultingDescriptor.annotations.hasAnnotation(COMPOSABLE_ANNOTATION)) return null
        if (resultingDescriptor.returnType?.isUnit() != true) return null

        val superCallTarget = getSuperCallExpression(resolvedCall.call)
        val callable = c.typeMapper.mapToCallableMethod(
            SamCodegenUtil.resolveSamAdapter(resultingDescriptor),
            superCallTarget != null,
            null,
            resolvedCall
        )

        val callGenerator = c.codegen.defaultCallGenerator

        val argumentGenerator = CallBasedArgumentGenerator(
            c.codegen,
            callGenerator,
            resultingDescriptor.valueParameters,
            callable.valueParameterTypes
        )

        return ComposableStackValue(
            c = c,
            argumentGenerator = argumentGenerator,
            resolvedCall = resolvedCall,
            callable = callable
        )
    }

}

private class ComposableStackValue(
    private val c: ExpressionCodegenExtension.Context,
    private val argumentGenerator: CallBasedArgumentGenerator,
    private val resolvedCall: ResolvedCall<*>,
    private val callable: CallableMethod
) : StackValue(c.typeMapper.mapType(resolvedCall.candidateDescriptor.returnType!!)) {

    private data class ParameterDescriptor(
        val descriptor: ValueParameterDescriptor,
        val type: Type,
        val storeIndex: Int,
        val isDefault: Boolean,
        val pivotal: Boolean,
        val stable: Boolean
    )

    override fun putSelector(type: Type, kotlinType: KotlinType?, v: InstructionAdapter) {
        val invokeLabel = Label()
        val skipLabel = Label()
        val endLabel = Label()

        // store the composer
        val composerType = Type.getType("Landroidx/compose/ViewComposer;")
        val composerStoreIndex = c.codegen.frameMap.enterTemp(composerType)
        v.getComposer()
        v.store(composerStoreIndex, composerType)

        val parameters = resolvedCall.valueArguments.toList()
            .sortedBy { it.first.index }
            .map { (descriptor, valueArgument) ->
                val asmType = callable.getValueParameters()[descriptor.index].asmType
                ParameterDescriptor(
                    descriptor = descriptor,
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
            "com/ivianuu/essentials/util/SourceLocationKt",
            "sourceLocation",
            "()Ljava/lang/Object;",
            false
        )

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

        val isSkippable = parameters.all { it.stable || it.isDefault }

        if (isSkippable) {
            parameters
                .filterNot { it.isDefault }
                .forEach { param ->
                    v.load(composerStoreIndex, composerType)
                    v.load(param.storeIndex, param.type)
                    v.ensureBoxed(param.type)
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
            defaultArgs.generateOnStackIfNeeded(c.codegen.defaultCallGenerator, false)

        if (!defaultMaskWasGenerated) {
            callable.genInvokeInstruction(v)
        } else {
            callable.genInvokeDefaultInstruction(v)
        }

        if (isSkippable) {
            // end invocation
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx/compose/ViewComposer", "endGroup", "()V", false)
            v.goTo(endLabel)

            // skip current group
            v.mark(skipLabel)
            v.load(composerStoreIndex, composerType)
            v.invokevirtual("androidx/compose/ViewComposer", "skipCurrentGroup", "()V", false)
        }

        // end group
        v.mark(endLabel)
        v.load(composerStoreIndex, composerType)
        v.invokevirtual("androidx/compose/ViewComposer", "endGroup", "()V", false)

        c.codegen.frameMap.leaveTemp(composerType)
    }
}

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

private fun writeUpdateScope(
    composable: FunctionDescriptor,
    c: ExpressionCodegenExtension.Context
) {
    val updateScopeDescriptor = ComposableUpdateScopeClassDescriptor(
        composable = composable,
        storageManager = LockBasedStorageManager.NO_LOCKS
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

    /*val codegenForUpdateScope = ImplementationBodyCodegen(
        null, classContextForUpdateScope, classBuilderForUpdateScope, c.codegen.state, c.codegen.parentCodegen, false)
*/
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
        asmType = c.typeMapper.mapType(updateScopeDescriptor)
    )

    classBuilderForUpdateScope.done()
}

private fun writeUpdateScopeConstructor(
    c: ExpressionCodegenExtension.Context,
    composable: FunctionDescriptor,
    codegen: FunctionCodegen,
    updateScopeClass: ComposableUpdateScopeClassDescriptor,
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
    updateScopeClass: ComposableUpdateScopeClassDescriptor,
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
        composable.valueParameters.forEachIndexed { index, param ->
            v.load(0, asmType)
            v.getfield(
                asmType.internalName,
                param.name.toString(),
                c.typeMapper.mapType(param.type).descriptor
            )
        }

        val type = composable.containingDeclaration.fqNameSafe.asString().replace(".", "/")

        v.invokestatic(type, composable.name.toString(), composable.computeJvmDescriptor(), false)
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