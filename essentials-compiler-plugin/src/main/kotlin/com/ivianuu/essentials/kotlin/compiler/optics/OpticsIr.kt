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

class OpticsIrGenerationExtension : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    val lens = pluginContext.referenceClass(Lens) ?: return
    val lensGet = lens.functions.single { it.owner.name.asString() == "get" }
    val lensSet = lens.functions.single { it.owner.name.asString() == "set" }
    moduleFragment.transformChildrenVoid(object : IrElementTransformerVoid() {
      override fun visitClass(declaration: IrClass): IrStatement {
        if (!declaration.isCompanion) return super.visitClass(declaration)
        val clazz = declaration.parent as IrClass
        if (!clazz.hasAnnotation(OpticsAnnotation)) return super.visitClass(declaration)
        val lensParametersWithFunction = clazz.primaryConstructor!!
          .valueParameters
          .map { parameter ->
            parameter to declaration.functions.single { function ->
              parameter.name == function.name
            }
          }
        lensParametersWithFunction.forEach { (parameter, function) ->
          val field = declaration.addField(
            function.name,
            function.returnType,
            DescriptorVisibilities.PRIVATE
          )
          field.initializer = DeclarationIrBuilder(pluginContext, field.symbol).run {
            val expr = irBlock {
              val lensImpl = IrFactoryImpl.buildClass {
                name = field.name
                visibility = DescriptorVisibilities.LOCAL
              }.apply clazz@{
                parent = scope.getLocalDeclarationParent()
                createImplicitParameterDeclarationWithWrappedDescriptor()
                superTypes += function.returnType

                addConstructor {
                  returnType = defaultType
                  isPrimary = true
                  visibility = DescriptorVisibilities.PUBLIC
                }.apply {
                  body = DeclarationIrBuilder(
                    pluginContext,
                    symbol
                  ).irBlockBody {
                    +irDelegatingConstructorCall(context.irBuiltIns.anyClass.constructors.single().owner)
                    +IrInstanceInitializerCallImpl(
                      UNDEFINED_OFFSET,
                      UNDEFINED_OFFSET,
                      this@clazz.symbol,
                      context.irBuiltIns.unitType
                    )
                  }
                }

                addFunction {
                  returnType = parameter.type
                  name = Name.identifier("get")
                }.apply {
                  addDispatchReceiver { type = defaultType }
                  overriddenSymbols += lensGet
                  val tParameter = addValueParameter("t", clazz.defaultType)
                  body = DeclarationIrBuilder(pluginContext, symbol).irBlockBody {
                    +irReturn(
                      irCall(
                        clazz.properties
                          .single { it.name == parameter.name }.getter!!
                      ).apply {
                        dispatchReceiver = irGet(tParameter)
                      }
                    )
                  }
                }

                addFunction {
                  returnType = clazz.defaultType
                  name = Name.identifier("set")
                }.apply {
                  addDispatchReceiver { type = defaultType }
                  overriddenSymbols += lensSet
                  val tParameter = addValueParameter("t", clazz.defaultType)
                  val vParameter = addValueParameter("v", parameter.type)
                  body = DeclarationIrBuilder(pluginContext, symbol).irBlockBody {
                    +irReturn(
                      irCall(clazz.functions.single { it.name.asString() == "copy" }).apply {
                        dispatchReceiver = irGet(tParameter)
                        putValueArgument(
                          symbol.owner.valueParameters
                            .indexOfFirst { it.name == parameter.name },
                          irGet(vParameter)
                        )
                      }
                    )
                  }
                }
              }

              +lensImpl
              +irCall(lensImpl.constructors.single())
            }

            irExprBody(expr)
          }

          function.body = DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            +irReturn(
              irGetField(
                irGetObject(declaration.symbol),
                field
              )
            )
          }
        }
        return super.visitClass(declaration)
      }
    })
  }
}
