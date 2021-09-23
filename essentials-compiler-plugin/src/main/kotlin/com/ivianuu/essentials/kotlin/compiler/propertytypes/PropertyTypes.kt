package com.ivianuu.essentials.kotlin.compiler.propertytypes

import com.ivianuu.injekt.compiler.*
import com.ivianuu.injekt.compiler.analysis.AnalysisContext
import com.ivianuu.injekt.compiler.analysis.InjectFunctionDescriptor
import com.ivianuu.injekt.compiler.resolution.*
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.rendering.*
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.load.kotlin.getJvmModuleNameForDeserializedDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.FunctionImportedFromObject
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.descriptorUtil.parentsWithSelf
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.utils.parentsWithSelf
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.util.slicedMap.BasicWritableSlice
import org.jetbrains.kotlin.util.slicedMap.RewritePolicy

fun MockProject.propertyTypes() {
  StorageComponentContainerContributor.registerExtension(
    this,
    object : StorageComponentContainerContributor {
      override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
      ) {
        container.useInstance(PropertyTypesDeclarationChecker())
      }
    }
  )
}

class PropertyTypesDeclarationChecker : DeclarationChecker, CallChecker {
  override fun check(
    declaration: KtDeclaration,
    descriptor: DeclarationDescriptor,
    context: DeclarationCheckerContext
  ) {
    if (descriptor !is PropertyDescriptor) return

    val analysisContext = AnalysisContext(context.moduleDescriptor.injektContext, context.trace)

    val propertyType = descriptor.returnType?.toTypeRef(context = analysisContext)
      ?: return

    val propertyTypeInfo = descriptor.propertyTypeInfo(analysisContext)
      ?: return

    if ((propertyTypeInfo.publicType != null &&
          !isSubType(propertyType, propertyTypeInfo.publicType, analysisContext)) ||
      (propertyTypeInfo.internalType != null &&
          !isSubType(propertyType, propertyTypeInfo.internalType, analysisContext)) ||
      (propertyTypeInfo.protectedType != null &&
          !isSubType(propertyType, propertyTypeInfo.protectedType, analysisContext))) {
      context.trace.report(
        PropertyTypeErrors.NOT_SUB_TYPE
          .on(declaration)
      )
    }
  }

  override fun check(
    resolvedCall: ResolvedCall<*>,
    reportOn: PsiElement,
    context: CallCheckerContext
  ) {
    val resultingDescriptor = resolvedCall.resultingDescriptor
    if (resultingDescriptor !is CallableDescriptor) return

    if (resultingDescriptor is PropertyDescriptor) {
      val analysisContext = AnalysisContext(context.moduleDescriptor.injektContext, context.trace)

      val propertyTypeInfo = resultingDescriptor.propertyTypeInfo(analysisContext)
        ?: return

      val exposedType = when {
        context.scope.parentsWithSelf
          .filterIsInstance<LexicalScope>()
          .mapNotNull { it.ownerDescriptor as? ClassDescriptor }
          .any { it in resultingDescriptor.parentsWithSelf } -> null
        resultingDescriptor.moduleName(analysisContext.injektContext) ==
            context.scope.ownerDescriptor.moduleName(analysisContext.injektContext) ->
          propertyTypeInfo.internalType ?: propertyTypeInfo.publicType
        else -> propertyTypeInfo.publicType
      } ?: return

      val expectedType = context.resolutionContext.expectedType
        .takeIf { it !== TypeUtils.NO_EXPECTED_TYPE }
        ?: resultingDescriptor.type

      if (expectedType !== TypeUtils.NO_EXPECTED_TYPE &&
          !isSubType(exposedType, expectedType.toTypeRef(context = analysisContext), analysisContext)) {
        context.trace.report(
          PropertyTypeErrors.NOT_SUB_TYPE
            .on(reportOn)
        )
      }
    } else {
      val analysisContext = AnalysisContext(context.moduleDescriptor.injektContext, context.trace)

      resolvedCall
        .valueArguments
        .values
        .flatMap { it.arguments }
        .mapNotNull { it.getArgumentExpression() }
        .mapNotNull { it.getResolvedCall(context.trace.bindingContext) }
        .filter { it.resultingDescriptor is PropertyDescriptor }
        .forEach { innerResolvedCall ->
          val resultingDescriptor = innerResolvedCall.resultingDescriptor as PropertyDescriptor
          val propertyTypeInfo = resultingDescriptor.propertyTypeInfo(analysisContext)
            ?: return@forEach

          val exposedType = when {
            context.scope.parentsWithSelf
              .filterIsInstance<LexicalScope>()
              .mapNotNull { it.ownerDescriptor as? ClassDescriptor }
              .any { it in resultingDescriptor.parentsWithSelf } -> null
            resultingDescriptor.moduleName(analysisContext.injektContext) ==
                context.scope.ownerDescriptor.moduleName(analysisContext.injektContext) ->
              propertyTypeInfo.internalType ?: propertyTypeInfo.publicType
            else ->
              propertyTypeInfo.publicType
          } ?: return

          val expectedType = innerResolvedCall.resultingDescriptor.returnType ?: return@forEach

          if (!isSubType(exposedType, expectedType.toTypeRef(context = analysisContext), analysisContext)) {
            context.trace.report(
              PropertyTypeErrors.NOT_SUB_TYPE
                .on(reportOn)
            )
          }
        }
      println()
    }
  }

  private fun isSubType(subType: TypeRef, superType: TypeRef, analysisContext: AnalysisContext): Boolean {
    val baseContext = subType.buildBaseContext(
      emptyList(),
      analysisContext
    )

    val context = subType.buildContext(baseContext, superType)

    return context.isOk
  }
}

fun DeclarationDescriptor.moduleName(context: InjektContext): String =
  getJvmModuleNameForDeserializedDescriptor(this)
    ?: context.module.name.asString()

fun PropertyDescriptor.propertyTypeInfo(context: AnalysisContext): PropertyTypeInfo? {
  context.trace?.get(PROPERTY_TYPE_INFO_SLICE, this)?.let { return it }

  if (isDeserializedDeclaration()) {
    val info = annotations
      .findAnnotation(PROPERTY_TYPE_INFO_ANNOTATION)
      ?.readChunkedValue()
      ?.decode<PersistedPropertyTypeInfo>()
      ?.toPropertyTypeInfo(context)
    if (info != null)
      context.trace?.record(PROPERTY_TYPE_INFO_SLICE, this, info)
    return info
  }

  val publicType = getter?.annotations?.findAnnotation(PUBLIC_TYPE_ANNOTATION)
    ?.type?.arguments?.single()?.type?.toTypeRef(context = context)
  val internalType = getter?.annotations?.findAnnotation(INTERNAL_TYPE_ANNOTATION)
    ?.type?.arguments?.single()?.type?.toTypeRef(context = context)
  val protectedType = getter?.annotations?.findAnnotation(PROTECTED_TYPE_ANNOTATION)
    ?.type?.arguments?.single()?.type?.toTypeRef(context = context)

  val info = PropertyTypeInfo(publicType, internalType, protectedType)

  context.trace?.record(PROPERTY_TYPE_INFO_SLICE, this, info)

  if (info.publicType != null || info.internalType != null || info.protectedType != null) {
    val serializedInfo = info.toPersistedPropertyTypeInfo(context)
      .encode()

    updateAnnotation(
      AnnotationDescriptorImpl(
        context.injektContext.module.findClassAcrossModuleDependencies(
          ClassId.topLevel(PROPERTY_TYPE_INFO_ANNOTATION)
        )!!.defaultType,
        serializedInfo.toChunkedAnnotationArguments(),
        SourceElement.NO_SOURCE
      )
    )
  }

  return info
}

private fun AnnotationDescriptor.readChunkedValue() = allValueArguments
  .toList()
  .sortedBy {
    it.first.asString()
      .removePrefix("value")
      .toInt()
  }
  .joinToString(separator = "") { it.second.value as String }

private fun String.toChunkedAnnotationArguments() = chunked(65535 / 2)
  .mapIndexed { index, chunk -> "value$index".asNameId() to StringValue(chunk) }
  .toMap()

private fun Annotated.updateAnnotation(annotation: AnnotationDescriptor) {
  val newAnnotations = Annotations.create(
    annotations
      .filter { it.type != annotation.type } + annotation
  )
  when (this) {
    is AnnotatedImpl -> updatePrivateFinalField<Annotations>(
      AnnotatedImpl::class,
      "annotations"
    ) { newAnnotations }
    is LazyClassDescriptor -> updatePrivateFinalField<Annotations>(
      LazyClassDescriptor::class,
      "annotations"
    ) { newAnnotations }
    is InjectFunctionDescriptor -> underlyingDescriptor.updateAnnotation(annotation)
    is FunctionImportedFromObject -> callableFromObject.updateAnnotation(annotation)
    else -> {
      //throw AssertionError("Cannot add annotation to $this $javaClass")
    }
  }
}

data class PropertyTypeInfo(
  val publicType: TypeRef?,
  val internalType: TypeRef?,
  val protectedType: TypeRef?
)

@Serializable data class PersistedPropertyTypeInfo(
  val publicType: PersistedTypeRef?,
  val internalType: PersistedTypeRef?,
  val protectedType: PersistedTypeRef?
)

private fun PropertyTypeInfo.toPersistedPropertyTypeInfo(context: AnalysisContext) =
  PersistedPropertyTypeInfo(
    publicType = publicType?.toPersistedTypeRef(context),
    internalType = internalType?.toPersistedTypeRef(context),
    protectedType = protectedType?.toPersistedTypeRef(context)
  )

private fun PersistedPropertyTypeInfo.toPropertyTypeInfo(context: AnalysisContext) =
  PropertyTypeInfo(
    publicType = publicType?.toTypeRef(context),
    internalType = internalType?.toTypeRef(context),
    protectedType = protectedType?.toTypeRef(context)
  )

private val PROPERTY_TYPE_INFO_SLICE =
  BasicWritableSlice<PropertyDescriptor, PropertyTypeInfo>(RewritePolicy.DO_NOTHING)

private val PUBLIC_TYPE_ANNOTATION = FqName("com.ivianuu.essentials.PublicType")
private val INTERNAL_TYPE_ANNOTATION = FqName("com.ivianuu.essentials.InternalType")
private val PROTECTED_TYPE_ANNOTATION = FqName("com.ivianuu.essentials.ProtectedType")
private val PROPERTY_TYPE_INFO_ANNOTATION = FqName("com.ivianuu.essentials.PropertyTypeInfo")


interface PropertyTypeErrors {
  companion object {
    @JvmField val MAP = DiagnosticFactoryToRendererMap("Injekt")

    @JvmField val NOT_SUB_TYPE =
      DiagnosticFactory0.create<PsiElement>(Severity.ERROR)
        .also {
          MAP.put(it, "property annotation type is not sub type of property type")
        }

    init {
      Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
        PropertyTypeErrors::class.java,
        PropertyTypesDefaultErrorMessages
      )
    }

    object PropertyTypesDefaultErrorMessages : DefaultErrorMessages.Extension {
      override fun getMap() = MAP
    }
  }
}