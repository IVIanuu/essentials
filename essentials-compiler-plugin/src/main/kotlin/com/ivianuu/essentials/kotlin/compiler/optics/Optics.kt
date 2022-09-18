/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler.optics

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeAttributes
import org.jetbrains.kotlin.types.TypeSubstitutor
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.asSimpleType
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.utils.addToStdlib.cast

fun optics(project: Project) {
  SyntheticResolveExtension.registerExtension(project, OpticsResolveExtension())
  IrGenerationExtension.registerExtension(project, OpticsIrGenerationExtension())
}

class OpticsResolveExtension : SyntheticResolveExtension {
  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
    if (thisDescriptor.annotations.hasAnnotation(OpticsAnnotation))
      SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
    else null

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
    if (thisDescriptor.isCompanionObject &&
      thisDescriptor.containingDeclaration.cast<ClassDescriptor>()
        .annotations.hasAnnotation(OpticsAnnotation)
    )
      thisDescriptor.containingDeclaration.cast<ClassDescriptor>()
        .unsubstitutedPrimaryConstructor
        ?.valueParameters
        ?.map { it.name }
        ?: emptyList()
    else emptyList()

  override fun generateSyntheticMethods(
    thisDescriptor: ClassDescriptor,
    name: Name,
    bindingContext: BindingContext,
    fromSupertypes: List<SimpleFunctionDescriptor>,
    result: MutableCollection<SimpleFunctionDescriptor>
  ) {
    if (!thisDescriptor.isCompanionObject) return
    val clazz = thisDescriptor.containingDeclaration as ClassDescriptor
    if (!clazz.annotations.hasAnnotation(OpticsAnnotation)) return
    val primaryConstructor = clazz.unsubstitutedPrimaryConstructor ?: return
    val parameter = primaryConstructor.valueParameters
      .singleOrNull { it.name == name } ?: return
    result += SimpleFunctionDescriptorImpl.create(
      thisDescriptor,
      Annotations.EMPTY,
      parameter.name,
      CallableMemberDescriptor.Kind.SYNTHESIZED,
      parameter.source
    ).apply {
      val typeParameters = clazz.declaredTypeParameters.map { typeParameter ->
        TypeParameterDescriptorImpl.createWithDefaultBound(
          this, Annotations.EMPTY, false, Variance.INVARIANT,
          typeParameter.name, typeParameter.index, LockBasedStorageManager.NO_LOCKS
        )
      }
      initialize(
        null,
        thisDescriptor.thisAsReceiverParameter,
        emptyList(),
        typeParameters,
        emptyList(),
        KotlinTypeFactory.simpleNotNullType(
          TypeAttributes.Empty,
          thisDescriptor.module.findClassAcrossModuleDependencies(
            ClassId.topLevel(Lens)
          )!!,
          listOf(
            clazz.defaultType.asSimpleType()
              .replace(
                newArguments = typeParameters
                  .map { it.defaultType.asTypeProjection() }
              )
              .asTypeProjection(),
            TypeSubstitutor.create(
              clazz.declaredTypeParameters
                .map { it.typeConstructor }
                .zip(typeParameters.map { it.defaultType.asTypeProjection() })
                .toMap()
            ).substitute(parameter.type, Variance.INVARIANT)
            !!.asTypeProjection()
          )
        ),
        Modality.FINAL,
        parameter.visibility,
        null
      )
    }
  }

  override fun getPossibleSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name>? =
    emptyList()

  override fun getSyntheticPropertiesNames(thisDescriptor: ClassDescriptor): List<Name> =
    emptyList()
}
