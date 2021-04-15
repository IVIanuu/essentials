package com.ivianuu.essentials.kotlin.compiler.optics

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addDispatchReceiver
import org.jetbrains.kotlin.ir.builders.declarations.addField
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrFactoryImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.asSimpleType
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.utils.addToStdlib.cast

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
                    .annotations.hasAnnotation(OpticsAnnotation))
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
        val lensType = KotlinTypeFactory.simpleNotNullType(
            Annotations.EMPTY,
            thisDescriptor.module.findClassAcrossModuleDependencies(
                ClassId.topLevel(Lens)
            )!!,
            listOf(
                clazz.defaultType.asSimpleType().asTypeProjection(),
                parameter.type.asTypeProjection()
            )
        )
        result += SimpleFunctionDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            parameter.name,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            clazz.source
        ).apply {
            initialize(
                null,
                thisDescriptor.thisAsReceiverParameter,
                emptyList(), // todo
                emptyList(),
                lensType,
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
                                            irCall(clazz.properties
                                                .single { it.name == parameter.name }.getter!!).apply {
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
                        +irReturn(irGetField(
                            irGetObject(declaration.symbol),
                            field
                        ))
                    }
                }
                return super.visitClass(declaration)
            }
        })
    }
}

private val OpticsAnnotation = FqName("com.ivianuu.essentials.optics.Optics")
private val Lens = FqName("com.ivianuu.essentials.optics.Lens")
