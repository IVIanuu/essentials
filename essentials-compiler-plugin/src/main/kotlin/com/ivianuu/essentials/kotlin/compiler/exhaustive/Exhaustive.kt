package com.ivianuu.essentials.kotlin.compiler.exhaustive

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.WhenMissingCase
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.MultiRenderer
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.types.TypeUtils

fun exhaustive(project: Project) {
  StorageComponentContainerContributor.registerExtension(
    project,
    object : StorageComponentContainerContributor {
      override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor
      ) {
        container.useInstance(ExhaustiveWhenChecker())
      }
    }
  )
}

class ExhaustiveWhenChecker : DeclarationChecker {
  override fun check(
    declaration: KtDeclaration,
    descriptor: DeclarationDescriptor,
    context: DeclarationCheckerContext
  ) {
    declaration.accept(object : KtTreeVisitorVoid() {
      override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)
        val whenType = WhenChecker.whenSubjectType(expression, context.trace.bindingContext)
        if (whenType != null &&
          (KotlinBuiltIns.isBoolean(TypeUtils.makeNotNullable(whenType)) ||
              WhenChecker.getClassDescriptorOfTypeIfEnum(whenType) != null ||
              DescriptorUtils.isSealedClass(TypeUtils.getClassDescriptor(whenType)))) {
          val missingCases = WhenChecker.getMissingCases(expression, context.trace.bindingContext)
          if (missingCases.isNotEmpty() && expression.elseExpression == null) {
            context.trace.report(ExhaustiveErrors.NOT_EXHAUSTIVE.on(expression, missingCases))
          }
        }
      }
    })
  }
}

interface ExhaustiveErrors {
  companion object {
    @JvmField val MAP = DiagnosticFactoryToRendererMap("Injekt")

    @JvmField
    val NOT_EXHAUSTIVE = DiagnosticFactory1.create<PsiElement, List<WhenMissingCase>>(Severity.ERROR)
      .also {
        MAP.put(
          it,
          "When is not exhaustive!\n\nMissing branches:\n{0}",
          object : MultiRenderer<List<WhenMissingCase>> {
            override fun render(a: List<WhenMissingCase>): Array<String> {
              return arrayOf(
                a.joinToString(prefix = "- ", separator = "\n- ") { it.branchConditionText },
              )
            }
          },
        )
      }

    init {
      Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
        ExhaustiveErrors::class.java,
        ExhaustiveDefaultErrorMessages
      )
    }

    object ExhaustiveDefaultErrorMessages : DefaultErrorMessages.Extension {
      override fun getMap() = MAP
    }
  }
}
