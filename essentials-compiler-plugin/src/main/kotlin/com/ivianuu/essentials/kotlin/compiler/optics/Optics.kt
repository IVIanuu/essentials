package com.ivianuu.essentials.kotlin.compiler.optics

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.ir.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.*
import org.jetbrains.kotlin.descriptors.impl.*
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.descriptorUtil.*
import org.jetbrains.kotlin.resolve.extensions.*
import org.jetbrains.kotlin.storage.*
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.*
import org.jetbrains.kotlin.utils.addToStdlib.*

fun MockProject.optics() {
  SyntheticResolveExtension.registerExtension(this, OpticsResolveExtension())
  IrGenerationExtension.registerExtension(this, OpticsIrGenerationExtension())
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
}

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

private val OpticsAnnotation = FqName("com.ivianuu.essentials.optics.Optics")
private val Lens = FqName("com.ivianuu.essentials.optics.Lens")
