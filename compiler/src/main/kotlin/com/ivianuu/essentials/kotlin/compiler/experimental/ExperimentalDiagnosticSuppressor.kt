package com.ivianuu.essentials.kotlin.compiler.experimental

import org.jetbrains.kotlin.com.intellij.openapi.extensions.*
import org.jetbrains.kotlin.com.intellij.openapi.project.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.diagnostics.*

fun experimental(project: Project) {
  @Suppress("DEPRECATION")
  Extensions.getRootArea().getExtensionPoint(DiagnosticSuppressor.EP_NAME)
    .registerExtension(ExperimentalDiagnosticSuppressor(), project)
}

class ExperimentalDiagnosticSuppressor : DiagnosticSuppressor {
  override fun isSuppressed(diagnostic: Diagnostic): Boolean =
    isSuppressed(diagnostic)

  override fun isSuppressed(diagnostic: Diagnostic, bindingContext: BindingContext?): Boolean =
    diagnostic.factory == Errors.OPT_IN_USAGE ||
        diagnostic.factory == Errors.OPT_IN_USAGE_ERROR ||
        diagnostic.factory == Errors.OPT_IN_USAGE_FUTURE_ERROR ||
        diagnostic.factory == Errors.OPT_IN_OVERRIDE ||
        diagnostic.factory == Errors.OPT_IN_OVERRIDE_ERROR
}
