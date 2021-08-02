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

package com.ivianuu.essentials.kotlin.compiler.serializationfix

import com.ivianuu.essentials.kotlin.compiler.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.com.intellij.openapi.extensions.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*
import org.jetbrains.kotlin.name.*

fun MockProject.serializationFix() {
  IrGenerationExtension.registerExtensionWithLoadingOrder(
    this,
    LoadingOrder.FIRST,
    object : IrGenerationExtension {
      override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
      ) {
        moduleFragment.transformChildrenVoid(
          object : IrElementTransformerVoid() {
            override fun visitClass(declaration: IrClass): IrStatement {
              val result = super.visitClass(declaration)
              if (!declaration.hasAnnotation(SerializerAnnotation)) return result
              declaration.constructors
                .flatMap { it.valueParameters }
                .flatMap { valueParameter ->
                  val allTypes = mutableSetOf<IrType>()
                  fun collectTypes(type: IrType) {
                    allTypes += type
                    (type as? IrSimpleType)?.arguments
                      ?.mapNotNull { it.typeOrNull }
                      ?.forEach { collectTypes(it) }
                  }
                  collectTypes(valueParameter.type)
                  allTypes
                    .mapNotNull { it.classOrNull?.owner }
                }
                .filterIsInstance<IrClass>()
                .filter { it.kind == ClassKind.ENUM_CLASS }
                .flatMap { it.declarations }
                .filterIsInstance<IrFunction>()
                .filter {
                  it.name.asString() == "values" &&
                      it.valueParameters.isEmpty()
                }
                .forEach {
                  it.origin = IrDeclarationOrigin.ENUM_CLASS_SPECIAL_MEMBER
                }
              return result
            }
          }
        )
      }
    }
  )
}

private val SerializerAnnotation = FqName("kotlinx.serialization.Serializable")
