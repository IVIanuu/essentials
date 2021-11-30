/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.kotlin.compiler.exhaustive

import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.cfg.*
import org.jetbrains.kotlin.com.intellij.openapi.project.*
import org.jetbrains.kotlin.com.intellij.psi.*
import org.jetbrains.kotlin.container.*
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.rendering.*
import org.jetbrains.kotlin.extensions.*
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.checkers.*
import org.jetbrains.kotlin.types.*

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
