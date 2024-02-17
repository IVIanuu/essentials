/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.extensions.*
import com.intellij.openapi.project.*
import com.ivianuu.essentials.kotlin.compiler.experimental.*
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.resolve.diagnostics.*
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.utils.addToStdlib.*

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
