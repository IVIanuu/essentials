/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.ivianuu.essentials.kotlin.compiler.optics.optics
import com.ivianuu.essentials.kotlin.compiler.serializationfix.serializationFix
import org.jetbrains.kotlin.utils.addToStdlib.cast

class EssentialsProjectInitializer : ProjectManagerListener {
  override fun projectOpened(project: Project) {
    optics(project.cast())
    serializationFix(project.cast())
  }
}
