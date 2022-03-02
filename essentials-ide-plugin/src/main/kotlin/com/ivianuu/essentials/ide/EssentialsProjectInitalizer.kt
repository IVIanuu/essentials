/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ide

import com.intellij.openapi.project.*
import com.ivianuu.essentials.kotlin.compiler.experimental.*
import com.ivianuu.essentials.kotlin.compiler.optics.*
import com.ivianuu.essentials.kotlin.compiler.serializationfix.*
import org.jetbrains.kotlin.utils.addToStdlib.*

class EssentialsProjectInitializer : ProjectManagerListener {
  override fun projectOpened(project: Project) {
    experimental(project.cast())
    optics(project.cast())
    serializationFix(project.cast())
  }
}
