package com.ivianuu.essentials.kotlin.compiler.optics

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.extensions.*

fun MockProject.optics() {
  SyntheticResolveExtension.registerExtension(this, OpticsResolveExtension())
  IrGenerationExtension.registerExtension(this, OpticsIrGenerationExtension())
}

val OpticsAnnotation = FqName("com.ivianuu.essentials.optics.Optics")
val Lens = FqName("com.ivianuu.essentials.optics.Lens")
