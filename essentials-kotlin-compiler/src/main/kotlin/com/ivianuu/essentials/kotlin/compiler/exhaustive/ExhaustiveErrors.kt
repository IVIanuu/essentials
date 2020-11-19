/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.kotlin.compiler.exhaustive

import org.jetbrains.kotlin.cfg.WhenMissingCase
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.MultiRenderer

interface ExhaustiveErrors {
    companion object {
        @JvmField
        val NOT_EXHAUSTIVE = DiagnosticFactory1.create<PsiElement, List<WhenMissingCase>>(Severity.ERROR)

        init {
            Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
                ExhaustiveErrors::class.java,
                ExhaustiveDefaultErrorMessages
            )
        }

        object ExhaustiveDefaultErrorMessages : DefaultErrorMessages.Extension {
            private val map = DiagnosticFactoryToRendererMap("Exhaustive").apply {
                put(
                    NOT_EXHAUSTIVE,
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
            override fun getMap() = map
        }
    }
}
