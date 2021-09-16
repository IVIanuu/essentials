/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.kotlin.compiler.optics

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
