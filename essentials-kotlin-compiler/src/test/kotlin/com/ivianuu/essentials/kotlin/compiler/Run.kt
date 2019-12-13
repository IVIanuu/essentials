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

package com.ivianuu.essentials.kotlin.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.JvmTarget
import org.junit.Test

class Run {

    @Test
    fun test() {
        val kotlinSource = SourceFile.kotlin(
            "KClass.kt", """ 
            import androidx.compose.Composable

            class KtClass { 
            
            @Composable
            fun myComposable() {
                otherComposable()
            }
            
            @Composable
            fun otherComposable(): Int = 0
            }
    """
        )
        KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            compilerPlugins = listOf<ComponentRegistrar>(EssentialsComponentRegistrar())
            inheritClassPath = true
            jvmTarget = JvmTarget.JVM_1_8.description
        }.compile()
    }

}