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

package com.ivianuu.essentials.kotlin.compiler.compose.overload

import com.ivianuu.essentials.kotlin.compiler.asClassName
import com.ivianuu.essentials.kotlin.compiler.asTypeName
import com.ivianuu.essentials.kotlin.compiler.compose.ComposableAnnotation
import com.ivianuu.essentials.kotlin.compiler.hasAnnotation
import com.ivianuu.essentials.kotlin.compiler.msg
import com.ivianuu.essentials.kotlin.compiler.report
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun createOverloadComposableDescriptor(
    descriptor: FunctionDescriptor,
    trace: BindingTrace
): OverloadComposableDescriptor? {
    if (!descriptor.annotations.hasAnnotation(ComposableAnnotation)) return null
    if (descriptor.returnType?.isUnit() != true) return null

    val overloads = descriptor.valueParameters
        .mapNotNull { valueParameter ->
            val overloads = valueParameter.annotations
                .filter { annotation ->
                    annotation.hasAnnotation(OverloadComposableMarkerAnnotation, descriptor.module)
                }

            if (valueParameter.returnType != valueParameter.builtIns.getFunction(0)
                && !valueParameter.returnType!!.annotations.hasAnnotation(ComposableAnnotation)
            ) {
                report(
                    valueParameter,
                    trace
                ) { MustBeComposableFunction0 }
                return null
            }

            if (overloads.isEmpty()) return@mapNotNull null

            if (overloads.size > 1) {
                report(
                    valueParameter,
                    trace
                ) { OnlyOneOverloadPerParam }
                return null
            }

            msg { "$valueParameter type ${valueParameter.returnType}" }

            val overload = overloads.single()

            val overloadDescriptor = descriptor.module.findClassAcrossModuleDependencies(
                ClassId.topLevel(overload.fqName!!)
            )!!

            val overloadCompanion = overloadDescriptor.companionObjectDescriptor
            if (overloadCompanion == null) {
                report(
                    valueParameter,
                    trace
                ) { NeedsACompanionObject }
                return null
            }

            if (overloadCompanion.getSuperInterfaces().none {
                    it.fqNameSafe == OverloadComposable
                }) {
                report(
                    valueParameter,
                    trace
                ) { CompanionObjectMustBeAOverloadComposable }
                return null
            }

            val overloadType = overloadCompanion.typeConstructor.supertypes
                .first { it.constructor.declarationDescriptor!!.fqNameSafe == OverloadComposable }
                .arguments
                .single()
                .type

            val overloadNames = valueParameter.annotations
                .filter { annotation ->
                    annotation.hasAnnotation(OverloadNameAnnotation, descriptor.module)
                }
            if (overloadNames.size > 1) {
                report(
                    valueParameter,
                    trace
                ) { OnlyOneOverloadNamePerParam }
                return null
            }
            val overloadName = overloadNames.singleOrNull()

            val finalOverloadName = overloadName?.allValueArguments
                ?.get(Name.identifier("name"))
                ?.value as? String
                ?: valueParameter.name.asString()

            return@mapNotNull OverloadDescriptor(
                annotationName = overloadDescriptor.asClassName(),
                name = finalOverloadName,
                type = overloadType.asTypeName()
            )
        }

    if (overloads.isEmpty()) return null

    return OverloadComposableDescriptor(
        packageName = descriptor.fqNameSafe.parent().asString(),
        fileName = descriptor.name.asString() + "__Overloads",
        overloads = overloads
    )
}