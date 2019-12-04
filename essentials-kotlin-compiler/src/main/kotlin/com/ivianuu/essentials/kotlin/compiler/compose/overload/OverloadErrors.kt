/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.kotlin.compiler.compose.overload

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap

val OnlyOneOverloadPerParam =
    error()
val OnlyOneOverloadNamePerParam =
    error()
val NeedsACompanionObject =
    error()
val CompanionObjectMustBeAOverloadComposable =
    error()
val MustBeComposableFunction0 =
    error()
val MustBeUnit = error()
val CannotHaveDefaultValueExceptNullable = error()

private fun error() = DiagnosticFactory0.create<PsiElement>(Severity.ERROR)

object OverloadComposableErrorMessages : DefaultErrorMessages.Extension {
    private val map = DiagnosticFactoryToRendererMap("OverloadComposable")
    override fun getMap(): DiagnosticFactoryToRendererMap =
        map

    init {
        map.put(
            OnlyOneOverloadPerParam,
            "Only one overload per param allowed"
        )
        map.put(
            OnlyOneOverloadNamePerParam,
            "Only one overload name per param allowed"
        )
        map.put(
            NeedsACompanionObject,
            "Needs a companion object"
        )
        map.put(
            CompanionObjectMustBeAOverloadComposable,
            "Companion object must a OverloadComposable"
        )
        map.put(
            MustBeComposableFunction0,
            "Type must be @Composable () -> Unit"
        )
        map.put(
            MustBeUnit,
            "Return type must be unit"
        )
        map.put(
            CannotHaveDefaultValueExceptNullable,
            "Cannot have default values except nullable"
        )
    }
}
