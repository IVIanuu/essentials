/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.ivianuu.essentials.kotlin.compiler.experimental.experimental
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.UnsafeCastFunction
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class EssentialsProjectInitializer : ProjectManagerListener {
  @OptIn(UnsafeCastFunction::class)
  override fun projectOpened(project: Project) {
    experimental(project.cast())

    @Suppress("DEPRECATION")
    Extensions.getRootArea().getExtensionPoint(DiagnosticSuppressor.EP_NAME)
      .registerExtension(
        object : DiagnosticSuppressor {
          override fun isSuppressed(diagnostic: Diagnostic): Boolean {
            // todo remove once compose fun interface support is fixed
            if (diagnostic.factory.name.contains("TYPE_MISMATCH") &&
              diagnostic is DiagnosticWithParameters2<*, *, *> &&
              diagnostic.a.safeAs<KotlinType>()?.isFunctionType == true &&
              diagnostic.b.safeAs<KotlinType>()?.isFunctionType == true)
              return true

            return false
          }
        }
      )
  }
}
