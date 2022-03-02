/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler.optics

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.com.intellij.openapi.project.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.*
import org.jetbrains.kotlin.descriptors.impl.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.extensions.*
import org.jetbrains.kotlin.storage.*
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.*
import org.jetbrains.kotlin.utils.addToStdlib.*

fun optics(project: Project) {
  SyntheticResolveExtension.registerExtension(project, OpticsResolveExtension())
  IrGenerationExtension.registerExtension(project, OpticsIrGenerationExtension())
}

class OpticsResolveExtension : SyntheticResolveExtension {
  override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
    if (thisDescriptor.annotations.hasAnnotation(OpticsAnnotation))
      SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
    else null

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
    return if (thisDescriptor.isCompanionObject &&
      thisDescriptor.containingDeclaration.cast<ClassDescriptor>()
        .annotations.hasAnnotation(OpticsAnnotation)
    )
      thisDescriptor.containingDeclaration.cast<ClassDescriptor>()
        .unsubstitutedPrimaryConstructor
        ?.valueParameters
        ?.map { it.name }
        ?: emptyList()
    else emptyList()
  }

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
        typeParameters,
        emptyList(),
        KotlinTypeFactory.simpleNotNullType(
          Annotations.EMPTY,
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
