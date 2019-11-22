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

import com.google.common.collect.Maps
import org.jetbrains.kotlin.codegen.AccessorForConstructorDescriptor
import org.jetbrains.kotlin.codegen.AccessorKind
import org.jetbrains.kotlin.codegen.CallGenerator
import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.SamCodegenUtil
import org.jetbrains.kotlin.codegen.coroutines.getOriginalSuspendFunctionView
import org.jetbrains.kotlin.codegen.coroutines.unwrapInitialDescriptorForSuspendFunction
import org.jetbrains.kotlin.codegen.extractReificationArgument
import org.jetbrains.kotlin.codegen.inline.InlineCodegenForDefaultBody
import org.jetbrains.kotlin.codegen.inline.PsiInlineCodegen
import org.jetbrains.kotlin.codegen.inline.PsiSourceCompilerForInline
import org.jetbrains.kotlin.codegen.inline.ReificationArgument
import org.jetbrains.kotlin.codegen.inline.TypeParameterMappings
import org.jetbrains.kotlin.codegen.intrinsics.IntrinsicMethod
import org.jetbrains.kotlin.codegen.signature.BothSignatureWriter
import org.jetbrains.kotlin.codegen.signature.JvmSignatureWriter
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper
import org.jetbrains.kotlin.codegen.unwrapInitialSignatureDescriptor
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.callUtil.usesDefaultArguments
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.resolve.jvm.jvmSignature.JvmMethodSignature
import org.jetbrains.kotlin.resolve.jvm.shouldHideConstructorDueToInlineClassTypeValueParameters
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.model.TypeParameterMarker
import org.jetbrains.kotlin.types.typesApproximation.approximateCapturedTypes
import org.jetbrains.org.objectweb.asm.Type

fun resolveToCallable(
    codegen: ExpressionCodegen,
    typeMapper: KotlinTypeMapper,
    fd: FunctionDescriptor,
    superCall: Boolean,
    resolvedCall: ResolvedCall<*>
): Callable {
    val intrinsic: IntrinsicMethod? = codegen.state.intrinsics.getIntrinsic(fd)
    return intrinsic?.toCallable(fd, superCall, resolvedCall, codegen)
        ?: typeMapper.mapToCallableMethod(
            SamCodegenUtil.resolveSamAdapter(
                fd
            ), superCall, null, resolvedCall
        )
}

fun accessibleFunctionDescriptor(
    codegen: ExpressionCodegen,
    resolvedCall: ResolvedCall<*>
): FunctionDescriptor? {
    var descriptor =
        resolvedCall.resultingDescriptor as FunctionDescriptor
    if (descriptor is TypeAliasConstructorDescriptor) {
        descriptor =
            descriptor.underlyingConstructorDescriptor
    }
    val originalIfSamAdapter =
        SamCodegenUtil.getOriginalIfSamAdapter(descriptor)
    if (originalIfSamAdapter != null) {
        descriptor = originalIfSamAdapter
    }
    descriptor =
        descriptor.unwrapInitialDescriptorForSuspendFunction()
    return if (resolvedCall.usesDefaultArguments()) { // $default method is not private, so you need no accessor to call it
        descriptor
    } else if (shouldHideConstructorDueToInlineClassTypeValueParameters(descriptor.original)) { // Constructors with inline class type value parameters should always be called using an accessor.
// NB this will require accessors even if the constructor itself is in a different module.
        AccessorForConstructorDescriptor(
            (descriptor.original as ClassConstructorDescriptor),
            descriptor.containingDeclaration,
            codegen.getSuperCallTarget(resolvedCall.call),
            AccessorKind.NORMAL
        )
    } else {
        codegen.context.accessibleDescriptor(
            descriptor, codegen.getSuperCallTarget(resolvedCall.call)
        )
    }
}


/*
fun invokeMethodWithArguments(
    codegen: ExpressionCodegen,
    callableMethod: Callable,
    resolvedCall: ResolvedCall<*>,
    receiver: StackValue,
    callGenerator: CallGenerator,
    argumentGenerator: ArgumentGenerator
) {
    if (isAssertCall(resolvedCall) && codegen.state.assertionsMode != JVMAssertionsMode.LEGACY) {
        generateAssert(codegen.state.assertionsMode, resolvedCall, codegen, codegen.parentCodegen)
        return
    }
    val isSuspendNoInlineCall =
        resolvedCall.isSuspendNoInlineCall(codegen, codegen.state.languageVersionSettings)
    val isConstructor =
        resolvedCall.resultingDescriptor is ConstructorDescriptor
    if (callableMethod !is IntrinsicWithSpecialReceiver) {
        codegen.putReceiverAndInlineMarkerIfNeeded(
            callableMethod,
            resolvedCall,
            receiver,
            isSuspendNoInlineCall,
            isConstructor
        )
    }
    callGenerator.processAndPutHiddenParameters(false)
    val valueArguments =
        resolvedCall.valueArgumentsByIndex
            ?: error("Failed to arrange value arguments by index: " + resolvedCall.resultingDescriptor)
    val defaultArgs = argumentGenerator.generate(
        valueArguments,
        ArrayList(
            resolvedCall.valueArguments.values
        ),
        resolvedCall.resultingDescriptor
    )

    val tailRecursionCodegen = TailRecursionCodegen(codegen.context, codegen, codegen.v, codegen.state)
    if (tailRecursionCodegen.isTailRecursion(resolvedCall)) {
        tailRecursionCodegen.generateTailRecursion(resolvedCall)
        return
    }
    val defaultMaskWasGenerated =
        defaultArgs.generateOnStackIfNeeded(callGenerator, isConstructor)
    // Extra constructor marker argument
    if (callableMethod is CallableMethod) {
        val callableParameters =
            callableMethod.getValueParameters()
        for (parameter in callableParameters) {
            if (parameter.kind == JvmMethodParameterKind.CONSTRUCTOR_MARKER) {
                callGenerator.putValueIfNeeded(
                    JvmKotlinType(parameter.asmType, null),
                    StackValue.constant(null, parameter.asmType)
                )
            }
        }
    }
    if (isSuspendNoInlineCall) {
        addSuspendMarker(v, true)
    }
    callGenerator.genCall(callableMethod, resolvedCall, defaultMaskWasGenerated, this)
    if (isSuspendNoInlineCall) {
        addReturnsUnitMarkerIfNecessary(v, resolvedCall)
        addSuspendMarker(v, false)
        addInlineMarker(v, false)
    }
    val returnType =
        resolvedCall.resultingDescriptor.returnType
    if (returnType != null && KotlinBuiltIns.isNothing(returnType)) {
        v.aconst(null)
        v.athrow()
    }
}*/

fun getOrCreateCallGenerator(
    codegen: ExpressionCodegen,
    typeMapper: KotlinTypeMapper,
    resolvedCall: ResolvedCall<*>
): CallGenerator {
    val descriptor = resolvedCall.resultingDescriptor

    val typeArguments =
        getTypeArgumentsForResolvedCall(
            resolvedCall,
            descriptor
        )
    val mappings =
        TypeParameterMappings<KotlinType>()
    for ((key, type) in typeArguments) {
        val isReified =
            key.isReified || InlineUtil.isArrayConstructorWithLambda(
                resolvedCall.resultingDescriptor
            )
        val typeParameterAndReificationArgument: Pair<TypeParameterMarker, ReificationArgument>? =
            codegen.typeSystem.extractReificationArgument(type)
        if (typeParameterAndReificationArgument == null) {
            val approximatedType =
                approximateCapturedTypes(type).upper
            // type is not generic
            val signatureWriter: JvmSignatureWriter =
                BothSignatureWriter(BothSignatureWriter.Mode.TYPE)
            val asmType: Type =
                typeMapper.mapTypeParameter(approximatedType, signatureWriter)
            mappings.addParameterMappingToType(
                key.name.identifier,
                approximatedType,
                asmType,
                signatureWriter.toString(),
                isReified
            )
        } else {
            mappings.addParameterMappingForFurtherReification(
                key.name.identifier,
                type,
                typeParameterAndReificationArgument.second,
                isReified
            )
        }
    }
    return getOrCreateCallGenerator(
        codegen,
        typeMapper,
        descriptor,
        resolvedCall.call.callElement,
        mappings,
        false
    )
}

private fun getOrCreateCallGenerator(
    codegen: ExpressionCodegen,
    typeMapper: KotlinTypeMapper,
    descriptor: CallableDescriptor,
    callElement: KtElement?,
    typeParameterMappings: TypeParameterMappings<KotlinType>?,
    isDefaultCompilation: Boolean
): CallGenerator {
    if (callElement == null) return codegen.defaultCallGenerator
    val isIntrinsic =
        descriptor is CallableMemberDescriptor &&
                codegen.state.intrinsics.getIntrinsic(descriptor) != null
    val isInline =
        InlineUtil.isInline(descriptor) && !isIntrinsic || InlineUtil.isArrayConstructorWithLambda(
            descriptor
        )
    // We should inline callable containing reified type parameters even if inline is disabled
// because they may contain something to reify and straight call will probably fail at runtime
    val shouldInline =
        isInline && (!codegen.state.isInlineDisabled || InlineUtil.containsReifiedTypeParameters(
            descriptor
        ))
    if (!shouldInline) return codegen.defaultCallGenerator
    val original: FunctionDescriptor =
        unwrapInitialSignatureDescriptor(
            DescriptorUtils.unwrapFakeOverride(descriptor.original as FunctionDescriptor)
        ).getOriginalSuspendFunctionView(codegen.bindingContext, codegen.state)
    val sourceCompiler =
        PsiSourceCompilerForInline(codegen, callElement)
    val functionDescriptor =
        if (InlineUtil.isArrayConstructorWithLambda(original)) FictitiousArrayConstructor.create(
            original as ConstructorDescriptor
        ) else original.original
    sourceCompiler.initializeInlineFunctionContext(functionDescriptor)
    val signature: JvmMethodSignature =
        typeMapper.mapSignatureWithGeneric(functionDescriptor, sourceCompiler.contextKind)
    val methodOwner: Type =
        typeMapper.mapImplementationOwner(functionDescriptor)
    return if (isDefaultCompilation) {
        InlineCodegenForDefaultBody(
            functionDescriptor,
            codegen,
            codegen.state,
            methodOwner,
            signature,
            sourceCompiler
        )
    } else {
        PsiInlineCodegen(
            codegen,
            codegen.state,
            functionDescriptor,
            methodOwner,
            signature,
            typeParameterMappings!!,
            sourceCompiler
        )
    }
}

private fun getTypeArgumentsForResolvedCall(
    resolvedCall: ResolvedCall<*>,
    descriptor: CallableDescriptor
): Map<TypeParameterDescriptor, KotlinType> {
    if (descriptor !is TypeAliasConstructorDescriptor) {
        return resolvedCall.typeArguments
    }
    val typeAliasConstructorDescriptor =
        descriptor
    val underlyingConstructorDescriptor =
        typeAliasConstructorDescriptor.underlyingConstructorDescriptor
    val resultingType =
        typeAliasConstructorDescriptor.returnType
    val typeArgumentsForReturnType =
        resultingType.arguments
    val typeParameters =
        underlyingConstructorDescriptor.typeParameters
    assert(typeParameters.size == typeArgumentsForReturnType.size) {
        "Type parameters of the underlying constructor " + underlyingConstructorDescriptor +
                "should correspond to type arguments for the resulting type " + resultingType
    }
    val typeArgumentsMap: MutableMap<TypeParameterDescriptor, KotlinType> =
        Maps.newHashMapWithExpectedSize(
            typeParameters.size
        )
    for (typeParameter in typeParameters) {
        val typeArgument =
            typeArgumentsForReturnType[typeParameter.index].type
        typeArgumentsMap[typeParameter] = typeArgument
    }
    return typeArgumentsMap
}

private class FictitiousArrayConstructor(arrayClass: ClassDescriptor) :
    SimpleFunctionDescriptorImpl(
        arrayClass.containingDeclaration,
        null,
        Annotations.EMPTY,
        arrayClass.name,
        CallableMemberDescriptor.Kind.SYNTHESIZED,
        SourceElement.NO_SOURCE
    ) {
    companion object Factory {
        @JvmStatic
        fun create(arrayConstructor: ConstructorDescriptor): FictitiousArrayConstructor {
            val arrayClass = arrayConstructor.constructedClass
            return FictitiousArrayConstructor(arrayClass).apply {
                this.initialize(
                    null,
                    null,
                    arrayConstructor.typeParameters,
                    arrayConstructor.valueParameters,
                    arrayClass.defaultType,
                    Modality.FINAL,
                    Visibilities.PUBLIC
                )
                this.isInline = true
            }
        }
    }
}
