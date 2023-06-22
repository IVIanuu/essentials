/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.ivianuu.essentials.kotlin.compiler.experimental.experimental
import org.jetbrains.kotlin.utils.addToStdlib.UnsafeCastFunction
import org.jetbrains.kotlin.utils.addToStdlib.cast

class EssentialsProjectInitializer : ProjectManagerListener {
  @OptIn(UnsafeCastFunction::class)
  override fun projectOpened(project: Project) {
    experimental(project.cast())
  }
}
